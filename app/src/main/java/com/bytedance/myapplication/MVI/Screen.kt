package com.bytedance.myapplication.MVI

// 定义屏幕路由,用于路由跳转界面
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")

    object Chat : Screen("chat")
//    项目页
    object Project : Screen("Project")
}