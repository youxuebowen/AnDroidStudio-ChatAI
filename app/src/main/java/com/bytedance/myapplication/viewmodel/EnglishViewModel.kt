package com.bytedance.myapplication.viewmodel
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
import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EnglishViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EnglishState())
    val uiState = _uiState.asStateFlow()
    fun handleIntent(intent: EnglishIntent) {
        when (intent) {
            is EnglishIntent.OpenCamera -> openCamera()
            is EnglishIntent.CloseCamera -> closeCamera()
//            is EnglishIntent.TakePicture -> takePicture()
//            is EnglishIntent.GenerateAudio -> generateAudio(intent.text)
        }
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

                            _uiState.update { it.copy(recognizedWord = topResultEn, isAnalyzing = false) }
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
    fun playAudio(text: String, context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingAudio = true) }
            try {
                val response = RetrofitClient.murfApi.generateSpeech(
                    apiKey = RetrofitClient.API_KEY,
                    requestBody = TtsRequest(text = text)
                )

                if (response.isSuccessful && response.body() != null) {
                    val file = saveStreamToTempFile(response.body()!!, context)
                    playMedia(file)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isGeneratingAudio = false) }
            }
        }
    }

    private fun playMedia(file: File) {
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

    fun reset() {
        _uiState.value = EnglishState()
    }

    // 修改：接收 LifecycleCameraController
    fun takePicture(controller: LifecycleCameraController, context: Context) {
        // 创建临时文件保存照片
        val photoFile = File(
            context.cacheDir,
            "english_capture_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        controller.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // 拍照成功，获取 Uri 并调用识别逻辑
                    val savedUri = Uri.fromFile(photoFile)
                    onImageCaptured(savedUri, context)
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                }
            }
        )
    }
    
    private fun saveStreamToTempFile(body: ResponseBody, context: Context): File {
        val file = File.createTempFile("tts_audio", ".mp3", context.cacheDir)
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
