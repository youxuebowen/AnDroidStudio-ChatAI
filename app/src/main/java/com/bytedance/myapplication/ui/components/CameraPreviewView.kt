package com.bytedance.myapplication.ui.components

import android.net.Uri
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException

@Composable
fun CameraPreviewView(
    // 暴露一个拍照的方法给外部，或者由内部按钮触发
    onImageCaptured: (Uri) -> Unit,
    // 我们可以通过这个参数让外部调用拍照
    controller: LifecycleCameraController = rememberCameraController()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // AndroidView 是 Compose 中承载原生 View 的容器
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

// 辅助方法：初始化 CameraController
@Composable
fun rememberCameraController(): LifecycleCameraController {
    val context = LocalContext.current
    return remember {
        LifecycleCameraController(context).apply {
            // 默认开启后置摄像头
            setEnabledUseCases(LifecycleCameraController.IMAGE_CAPTURE)
        }
    }
}