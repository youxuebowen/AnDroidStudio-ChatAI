package com.bytedance.myapplication.MVI
import android.net.Uri
data class EnglishState(
    val capturedImageUri: Uri? = null,
    val recognizedWord: String = "",
    val isGeneratingAudio: Boolean = false,
    val isAnalyzing: Boolean = false,
    val isCameraOpen: Boolean = false,
)
