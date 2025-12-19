package com.bytedance.myapplication.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import com.bytedance.myapplication.MVI.EnglishState
import com.bytedance.myapplication.Network.RetrofitClient
import com.bytedance.myapplication.Network.TtsRequest

class EnglishViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EnglishState())
    val uiState = _uiState.asStateFlow()

    // 1. 处理拍照后的识别
    fun onImageCaptured(uri: Uri, context: Context) {
        _uiState.update { it.copy(capturedImageUri = uri, isAnalyzing = true) }

        try {
            val image = InputImage.fromFilePath(context, uri)
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

            labeler.process(image)
                .addOnSuccessListener { labels ->
                    val result = labels.firstOrNull()?.text ?: "Unknown"
                    _uiState.update { it.copy(recognizedWord = result, isAnalyzing = false) }
                }
                .addOnFailureListener {
                    _uiState.update { it.copy(isAnalyzing = false) }
                }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { it.copy(isAnalyzing = false) }
        }
    }

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