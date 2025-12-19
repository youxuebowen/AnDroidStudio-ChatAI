package com.bytedance.myapplication.MVI

import com.bytedance.myapplication.R

// 定义屏幕路由,用于路由跳转界面
sealed class Screen(val route: String,
                    val label: String,
                    val iconResId: Int ) {
    object Onboarding : Screen("onboarding", "Onboarding", R.drawable.icon_anboarding)
    object Login : Screen("login","Login", R.drawable.icon_login)
    object Register : Screen("register","Register",R.drawable.icon_anboarding)

    object Chat : Screen("chat","聊天", R.drawable.icon_chat)
//    项目页
    object Project : Screen("Project","热点", R.drawable.ic_project)

    object English : Screen("English","拍摄",R.drawable.icon_cammera)

    object Review : Screen("Review","复习",R.drawable.icon_school)

}