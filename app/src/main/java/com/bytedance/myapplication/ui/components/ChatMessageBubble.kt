package com.bytedance.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bytedance.myapplication.data.ChatMessage
import kotlinx.coroutines.delay

@Composable
private fun AnimatedText(message: ChatMessage) {
    // 对AI回复使用打字机效果，对用户消息直接显示全部内容
    if (message.isFromUser) {
        Text(
            text = message.text,
            modifier = Modifier.padding(12.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge
        )
        return
    }
    
    // 只对AI消息应用打字机效果
    // 使用rememberSaveable确保在配置更改时保持状态
    var displayedText by remember { mutableStateOf("") }
    var currentIndex by remember { mutableStateOf(0) }
    
    // 根据文本长度动态调整打字速度
    val typingDelay = when {
        message.text.length > 500 -> 5 // 长文本时更快
        message.text.length > 200 -> 10
        message.text.length > 50 -> 20
        else -> 30 // 短文本时更慢，体验更好
    }
    
    // 关键改进：当message.text更新时，保留当前显示的内容，继续显示新增部分
    LaunchedEffect(message.text) {
        // 如果内容被更新（流式API返回更多内容），但当前显示的内容是message.text的前缀
        // 则从当前位置继续显示
        if (message.text.startsWith(displayedText) && currentIndex <= message.text.length) {
            // 继续显示剩余内容
            while (currentIndex < message.text.length) {
                displayedText = message.text.substring(0, currentIndex + 1)
                currentIndex++
                delay(typingDelay.toLong())
            }
        } else {
            // 如果内容不是前缀（例如全新消息或内容被替换），则重新开始
            displayedText = ""
            currentIndex = 0
            while (currentIndex < message.text.length) {
                displayedText = message.text.substring(0, currentIndex + 1)
                currentIndex++
                delay(typingDelay.toLong())
            }
        }
    }
    
    // 显示动画文本
    Text(
        text = displayedText,
        modifier = Modifier.padding(12.dp),
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                bottomEnd = if (message.isFromUser) 4.dp else 16.dp
            ),
            color = if (message.isFromUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            },
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // 打字机效果实现
            AnimatedText(message = message)
        }
    }
}