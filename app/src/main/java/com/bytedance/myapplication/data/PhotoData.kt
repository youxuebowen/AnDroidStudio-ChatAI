package com.bytedance.myapplication.data

import android.net.Uri

data class PhotoData(
    val capturedImageUri: Uri? = null,    // 拍照后的图片地址
    val recognizedWord: String = "",      // 识别出的英文单词
    val isAnalyzing: Boolean = false,     // 是否正在识别物体
    val isGeneratingAudio: Boolean = false // 是否正在生成语音
)
//data class TtsRequest(
//    val text: String,
//    val voiceId: String = "en-US-1" // 示例默认值
//)