package com.bytedance.myapplication.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CammeraComponent(
    capturedUri: Uri?,
    isAnalyzing: Boolean,
    onCaptureClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (capturedUri == null) {
            Button(onClick = onCaptureClick) {
                Text("Capture")
            }
        } else {
            Button(onClick = onResetClick) {
                Text("Reset")
            }
        }
    }
}