package com.bytedance.myapplication.viewmodel
import android.app.Application
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.label.ImageLabeling
//import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.bytedance.myapplication.Network.RetrofitClientBaidu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import com.bytedance.myapplication.MVI.EnglishIntent
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import com.bytedance.myapplication.MVI.EnglishState
import com.bytedance.myapplication.Network.RetrofitClient
import com.bytedance.myapplication.Network.TtsRequest
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytedance.myapplication.Repository.EnglishRespositoryHistory
import com.bytedance.myapplication.data.database.EnglishEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class EnglishViewModel(
    private val englishRespositoryHistory: EnglishRespositoryHistory,
    application: Application
) : AndroidViewModel(application) {
    val historyList: StateFlow<List<EnglishEntity>> = englishRespositoryHistory.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private val _uiState = MutableStateFlow(EnglishState())

    val uiState = _uiState.asStateFlow()
    // 现在你可以通过 getApplication<Application>() 随时获取 context
    private val context: Context get() = getApplication<Application>().applicationContext
    // 1. 获取历史数据的 Flow 并转为 StateFlow
    // 这样只要数据库变动，historyList 会自动更新
    fun handleIntent(intent: EnglishIntent) {
        when (intent) {
            is EnglishIntent.OpenCamera -> openCamera()
            is EnglishIntent.CloseCamera -> closeCamera()
            is EnglishIntent.Review -> review()
//            is EnglishIntent.TakePicture -> takePicture()
//            is EnglishIntent.GenerateAudio -> generateAudio(intent.text)
        }
    }
    private fun review(){
        _uiState.update { it.copy(isShowingHistory = true, historyList = historyList.value) }
    }
    private fun openCamera() {
        _uiState.update { it.copy(isCameraOpen = true) }
    }
    private fun closeCamera(){
        _uiState.update { it.copy(isCameraOpen = false) }
    }
    private fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            // 百度 API 不需要 "data:image/jpeg;base64," 前缀，直接传纯编码
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun translateChineseToEnglish(text: String): String {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.CHINESE)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        val translator = Translation.getClient(options)

        return try {
            // 确保模型已下载（首次运行会自动下载）
            translator.downloadModelIfNeeded().await()
            // 执行翻译
            val result = translator.translate(text).await()
            result
        } catch (e: Exception) {
            text // 如果翻译失败，返回原词
        } finally {
            translator.close()
        }
    }
    // 在 ViewModel 中
    fun onImageCaptured(uri: Uri, context: Context) {
        // 1. 进入识别状态
        _uiState.update { it.copy(capturedImageUri = uri, isAnalyzing = true) }

        // 使用协程处理耗时的图片转换和网络请求
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 2. 将图片转换为 Base64
                val base64String = uriToBase64(context, uri)

                if (base64String != null) {
                    // 3. 调用百度 API
                    // 注意：Bearer 后面记得加空格，Token 建议动态获取
                    val token = "Bearer bce-v3/ALTAK-gG20qC2j41qu1hVeCJhfC/1d5ae34088cd8df874140a2a4ca21e4a84b812d2"
                    try {
                        // 增加 2 秒延迟

                        val response = RetrofitClientBaidu.baiduApi.classifyImage(
                            auth = token,
                            imageBase64 = base64String
                        )
                        // 4. 解析结果并更新 UI (切回主线程)
                        withContext(Dispatchers.Main) {
                            val topResultCh = response.result.firstOrNull()?.keyword ?: "Unknown"
                            Log.d("BaiduAI", "识别结果: $topResultCh") // 输出成功翻译的日志
                            // 5. 翻译 (在 IO 线程或当前协程中执行)
                            val topResultEn = if (topResultCh != "Unknown") {
                                translateChineseToEnglish(topResultCh)

                            } else {
                                "Unknown"
                            }
                            Log.d("BaiduAI", "翻译结果: $topResultEn") // 输出成功翻译的日志

                            _uiState.update { it.copy(recognizedWord = topResultEn,recognizedCn = topResultCh, isAnalyzing = false) }
                            saveEnglishToDatabase(topResultEn,topResultCh,_uiState.value.capturedImageUri?.path.toString())
                            playAudio(topResultEn,)
                        }
                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(isAnalyzing = false) }
                        // 提示：图片转换失败
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isAnalyzing = false) }
                }
            }
        }
    }


    // 1. 处理拍照后的识别
//    fun onImageCaptured(uri: Uri, context: Context) {
//        _uiState.update { it.copy(capturedImageUri = uri, isAnalyzing = true) }
//
//        try {
//            val image = InputImage.fromFilePath(context, uri)
////            labeler：初始化 Google ML Kit 的图像标签识别器。
////            这里使用的是 DEFAULT_OPTIONS，意味着它使用手机本地的通用模型（约能识别 400+ 种常见的物体，如：植物、家具、食物等）。
//            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
//
//            labeler.process(image)
//                .addOnSuccessListener { labels ->
//                    val result = labels.firstOrNull()?.text ?: "Unknown"
//                    _uiState.update { it.copy(recognizedWord = result, isAnalyzing = false) }
//                }
//                .addOnFailureListener {
//                    _uiState.update { it.copy(isAnalyzing = false) }
//                }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            _uiState.update { it.copy(isAnalyzing = false) }
//        }
//    }

    // 2. 调用你的 Murf.ai 接口 (对照你的 Python 逻辑)
    fun playAudio(text: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingAudio = true) }
            try {
                val response = RetrofitClient.murfApi.generateSpeech(
                    apiKey = RetrofitClient.API_KEY,
                    requestBody = TtsRequest(text = text)
                )

                if (response.isSuccessful && response.body() != null) {
                    // 保存语音到永久目录
                    val audioFile = saveAudioToPermanent(response.body()!!, context)
                    // 更新数据库中的音频路径
                    if (_uiState.value.currentRecordId != -1L) {
                        englishRespositoryHistory.updateAudioPath(_uiState.value.currentRecordId, audioFile.absolutePath)
                    }
                    _uiState.update { it.copy(voiceUri = audioFile.absolutePath) }
                    withContext(Dispatchers.Main) { playMedia(audioFile) }
//                    val file = saveStreamToTempFile(response.body()!!, context)
//                    playMedia(file)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isGeneratingAudio = false) }
            }
        }
    }


        fun playMedia(file: File) {
        try {
            MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    release()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun playAudioByPath(path: String?) {
        if (path.isNullOrEmpty()) {
            Log.e("AudioPlayer", "路径为空，无法播放")
            return
        }

        val file = File(path)
        if (!file.exists()) {
            Log.e("AudioPlayer", "文件不存在: $path")
            return
        }

        try {
            MediaPlayer().apply {
                // 设置数据源（这里直接传入绝对路径字符串）
                setDataSource(path)

                // 准备播放（同步准备，如果是网络资源建议用 prepareAsync）
                prepare()

                // 开始播放
                start()

                // 播放完成后释放资源，防止内存泄漏和占用
                setOnCompletionListener { mp ->
                    mp.release()
                    Log.d("AudioPlayer", "播放完成，资源已释放")
                }

                // 错误监听
                setOnErrorListener { mp, what, extra ->
                    Log.e("AudioPlayer", "播放出错: what=$what, extra=$extra")
                    mp.release()
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "播放异常: ${e.message}")
            e.printStackTrace()
        }
    }

    fun reset() {
        _uiState.value = EnglishState()
    }

    // 修改：接收 LifecycleCameraController
    fun takePicture(controller: LifecycleCameraController, context: Context) {
        // 使用 filesDir 存储，避免被系统清理
        val permanentFolder = File(context.filesDir, "captured_images").apply { mkdirs() }
        val photoFile = File(permanentFolder, "IMG_${System.currentTimeMillis()}.jpg")
//        val absolutePath = photoFile.absolutePath
//        Log.d("StoragePath", "图片已保存至: $absolutePath")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        controller.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    onImageCaptured(savedUri, context)
                    _uiState.update { it.copy(capturedImageUri = savedUri) }
                }
                override fun onError(exception: ImageCaptureException) { /* 处理错误 */ }
            }
        )
    // 创建临时文件保存照片
//        val photoFile = File(
//            context.cacheDir,
//            "english_capture_${System.currentTimeMillis()}.jpg"
//        )
//
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//        controller.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(context),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    // 拍照成功，获取 Uri 并调用识别逻辑
//                    val savedUri = Uri.fromFile(photoFile)
//                    onImageCaptured(savedUri, context)
//                }
//
//                override fun onError(exception: ImageCaptureException) {
//                    exception.printStackTrace()
//                }
//            }
//        )
    }
    // 2. 识别成功后存入数据库
    private fun saveEnglishToDatabase(en: String, cn: String, imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val record = EnglishEntity(
                wordEn = en,
                wordCn = cn,
                imagePath = imagePath,
                audioPath = null // 音频还没生成，先传 null
            )
            val id = englishRespositoryHistory.insertEnglish(record)
            _uiState.update { it.copy(currentRecordId = id) }
        }
    }

    private fun saveAudioToPermanent(body: ResponseBody, context: Context): File {
        val folder = File(context.filesDir, "voices").apply { mkdirs() }
        val file = File(folder, "VOICE_${System.currentTimeMillis()}.mp3")
//        val file = File.createTempFile("tts_audio", ".mp3", context.cacheDir)
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)
            val buffer = ByteArray(4096)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
        return file
    }
}
    class EnglishViewModelFactory(
        private val repository: EnglishRespositoryHistory,
        private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // 检查请求的 ViewModel 是否是 EnglishViewModel 或其子类
            if (modelClass.isAssignableFrom(EnglishViewModel::class.java)) {
                return EnglishViewModel(repository, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
