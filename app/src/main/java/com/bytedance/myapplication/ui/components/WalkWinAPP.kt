package com.bytedance.myapplication.ui.components
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bytedance.myapplication.ui.SplashScreen
import androidx.navigation.compose.rememberNavController
import com.bytedance.myapplication.ui.LoginScreen
import com.bytedance.myapplication.MVI.Screen
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ScaleFactor
import com.bytedance.myapplication.ui.ChatScreen
import com.bytedance.myapplication.viewmodel.ChatViewModel
import com.bytedance.myapplication.viewmodel.SplashViewModel
import com.bytedance.myapplication.viewmodel.EnglishViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bytedance.myapplication.ui.ProjectScreen
import com.bytedance.myapplication.ui.EnglishScreen
import com.bytedance.myapplication.ui.ReviewScreen

@Composable
fun WalkWinApp(viewModel:ChatViewModel,englishViewmodel:EnglishViewModel) {                                   // ← 这就是你的 App 启动后显示的所有界面
    val navController = rememberNavController()     // ← 导航控制器（相当于一个遥控器）
    var isSplashFinished by remember { mutableStateOf(false) }
    if (!isSplashFinished) {
        val splashViewModel: SplashViewModel = viewModel()
        SplashScreen(
            viewModel = splashViewModel,
            onFinished = { isSplashFinished = true }
        )
    } else {
        NavHost(
            navController = navController,           // ← 导航主机（电视机）
            startDestination = Screen.Onboarding.route
        ) {  // ← 一打开 App 先看哪个页面

            // 下面这些就是“遥控器能切到哪些频道”

            composable(Screen.Onboarding.route) {        // ← 当网址是 onboarding 时
                OnboardingScreen(                        //     显示引导页
                    onLoginClick = {                     //     用户点“去登录”按钮时
                        navController.navigate(Screen.Login.route)  // → 跳转到登录页
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onLoginSuccess = {
                        navController.navigate(Screen.Chat.route) {
                            // 防止回退到登录/引导页
                            popUpTo(Screen.Onboarding.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(Screen.Chat.route){
                ChatScreen(viewModel = viewModel, navController = navController)
            }
            composable(Screen.Project.route){
                ProjectScreen(chatViewModel= viewModel , navController = navController)
            }
            composable(Screen.English.route){
                EnglishScreen(viewModel = englishViewmodel,navController = navController)
            }
            composable(Screen.Review.route){
                ReviewScreen(viewModel = englishViewmodel,navController = navController)
            }

            // 你以后还可以在下面继续加：
            // composable("home") { HomeScreen() }
            // composable("profile") { ProfileScreen() }
            // ……
        }
    }
}