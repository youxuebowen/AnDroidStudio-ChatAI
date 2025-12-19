package com.bytedance.myapplication.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bytedance.myapplication.R
@Composable
fun ControlButtons(
    capturedUri: Uri?,
    isAnalyzing: Boolean,
    onCaptureClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // 使用 LargeFloatingActionButton 或自定义 Button 实现大按钮效果
        Button(
            onClick = { if (capturedUri == null) onCaptureClick() else onResetClick() },
            modifier = Modifier
                .size(80.dp), // 设置较大的尺寸
            shape = CircleShape, // 设置为正圆
            colors = ButtonDefaults.buttonColors(
                containerColor = if (capturedUri == null) Color.White else MaterialTheme.colorScheme.errorContainer,
                contentColor = if (capturedUri == null) Color.Black else MaterialTheme.colorScheme.onErrorContainer
            ),
            contentPadding = PaddingValues(0.dp), // 消除内部默认边距，确保图标居中
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            if (isAnalyzing) {
                // 如果正在识别，显示加载动画
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            } else {
                // 根据状态显示不同的本地图标
                val iconRes = if (capturedUri == null) {
                    R.drawable.camera // 你本地的拍照图标
                } else {
                    R.drawable.camera // 你本地的重置/返回图标
                }

                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = if (capturedUri == null) "Capture" else "Reset",
                    modifier = Modifier.size(42.dp), // 图标也相应放大
                    tint = if (capturedUri == null) Color.Unspecified else Color.Red // 保持拍照图标原色或自定义
                )
            }
        }
    }
}