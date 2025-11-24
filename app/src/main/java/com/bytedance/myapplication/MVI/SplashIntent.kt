package com.bytedance.myapplication.MVI
/*首页Intent*/
sealed class SplashIntent {
    object StartTimer : SplashIntent()
}