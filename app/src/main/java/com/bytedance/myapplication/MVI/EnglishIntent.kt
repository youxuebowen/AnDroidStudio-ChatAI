package com.bytedance.myapplication.MVI

sealed class EnglishIntent {
    object OpenCamera : EnglishIntent()
    object CloseCamera : EnglishIntent()
//    object TakePicture : EnglishIntent()
//    data class GenerateAudio(val text: String) : EnglishIntent()
}
