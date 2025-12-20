package com.bytedance.myapplication.ui

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.bytedance.myapplication.MVI.EnglishIntent
import com.bytedance.myapplication.R
import com.bytedance.myapplication.ui.components.CameraPreviewView
import com.bytedance.myapplication.ui.components.ControlButtons
import com.bytedance.myapplication.ui.components.EnglishBottomBar
import com.bytedance.myapplication.ui.components.WordResultCard
import com.bytedance.myapplication.ui.components.rememberCameraController
import com.bytedance.myapplication.viewmodel.EnglishViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EnglishScreen(viewModel: EnglishViewModel = viewModel(), navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 创建相机控制器
    val controller = rememberCameraController()

    // 权限处理 (使用 Accompanist)
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    Scaffold(
        bottomBar = {
            EnglishBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    // 执行导航逻辑
                    navController.navigate(route) {
                        // 避免在返回栈中堆积目的地
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
//        如果有相机权限
        if (cameraPermissionState.status.isGranted) {
            if(uiState.isCameraOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding) // 确保内容不被底部导航栏遮挡
                ) {

                    // 1. 底层：相机预览或展示已拍照片
                    if (uiState.capturedImageUri == null) {
                        CameraPreviewView(
                            onImageCaptured = { uri ->
                                viewModel.onImageCaptured(uri, context)
                            },
                            controller = controller // 将控制器传递给预览视图
                        )
                    } else {
                        AsyncImage(
                            model = uiState.capturedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // 2. 中间层：识别结果卡片 (仅在有结果时显示)
                    if (uiState.recognizedWord.isNotEmpty()) {
                        WordResultCard(
                            word = uiState.recognizedWord,
                            wordCn = uiState.recognizedCn,
                            isGenerating = uiState.isGeneratingAudio,
                            onPlayClick = { viewModel.playAudioByPath(uiState.voiceUri) },
                            onResetClick = { viewModel.reset() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

//                    // 3. 顶层：操作按钮
                    ControlButtons(
                        capturedUri = uiState.capturedImageUri,
                        isAnalyzing = uiState.isAnalyzing,
                        onCaptureClick = { viewModel.takePicture(controller, context) }, // 传递 controller
                        onResetClick = { viewModel.reset() },
                        onExitClick = {viewModel.handleIntent(EnglishIntent.CloseCamera)},
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 120.dp) // 确保它在悬浮导航栏的上方，不遮挡导航栏
                    )
                }
            } else {
                // 当相机未打开时，显示打开相机的按钮
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { viewModel.handleIntent(EnglishIntent.OpenCamera) },
                        modifier = Modifier.size(120.dp) // 设置按钮大小
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.camera_big), // 请确保此资源存在
                            contentDescription = "Open Camera",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }


        } else {
            // 未授予权限时的显示
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                PermissionRequestView { cameraPermissionState.launchPermissionRequest() }
            }
        }
    }
}


@Composable
fun PermissionRequestView(onRequestPermission: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = onRequestPermission) {
            Text("Request Camera Permission")
        }
    }
}