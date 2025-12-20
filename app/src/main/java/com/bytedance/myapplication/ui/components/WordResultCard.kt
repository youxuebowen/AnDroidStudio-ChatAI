package com.bytedance.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WordResultCard(
    word: String,               // 识别出的单词
    wordCn : String,
    isGenerating: Boolean,      // 是否正在调用 Murf.ai 生成语音
    onPlayClick: () -> Unit,    // 点击播放的回调
    onResetClick: () -> Unit,   // 点击关闭/重置的回调
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        color = Color.Black.copy(alpha = 0.8f), // 稍微深一点的透明度，增强对比度
        shape = RoundedCornerShape(24.dp),      // 更圆润的角，符合现代审美
        tonalElevation = 8.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {

            // 右上角的关闭按钮，方便用户返回相机
            IconButton(
                onClick = onResetClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White.copy(alpha = 0.6f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 英文单词显示
                Text(
                    text = word,
                    fontSize = 36.sp, // 加大字号
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 42.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                // 中文单词显示
                Text(
                    text = wordCn,
                    fontSize = 18.sp, // 加大字号
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 42.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 发音播放按钮
                Button(
                    onClick = onPlayClick,
                    enabled = !isGenerating,
                    modifier = Modifier
                        .fillMaxWidth(0.7f) // 按钮占卡片宽度的 70%
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isGenerating) {
                        // 加载状态：小圆圈，大小适配文字
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("正在生成...", fontSize = 16.sp)
                    } else {
                        // 正常状态：图标 + 文字
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "听发音",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}