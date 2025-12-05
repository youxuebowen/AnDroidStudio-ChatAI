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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bytedance.myapplication.data.ChatMessage
import com.bytedance.myapplication.MVI.ChatIntent
import com.bytedance.myapplication.viewmodel.ChatViewModel
import kotlinx.coroutines.delay

@Composable
private fun AnimatedText(message: ChatMessage, streamingMessageId: Long?, viewModel: ChatViewModel) {
    // 为SSE流消息实现实时协同的打字机效果
    var displayedText by remember(message.messageId) {
        mutableStateOf(message.text)
    }
    var isTyping by remember(message.messageId) {
        mutableStateOf(false)
    }

    LaunchedEffect(message.text, streamingMessageId) {
        if (message.isFromUser) {
            // 用户消息直接显示完整内容
            displayedText = message.text
            isTyping = false
            // 发送打字状态更新给ViewModel
            viewModel.handleIntent(ChatIntent.UpdateTypingStatus(message.messageId, false))
        } else {
            // 只有当消息是当前正在流式接收的消息时，才应用打字机效果
            val isStreamingMessage = message.messageId == streamingMessageId
            
            if (isStreamingMessage) {
                // 当前正在接收的AI消息，应用打字机效果
                isTyping = true
                // 发送打字状态更新给ViewModel
                viewModel.handleIntent(ChatIntent.UpdateTypingStatus(message.messageId, true))
                
                // 只显示新增加的字符，避免重复显示
                val newChars = message.text.substring(displayedText.length)
                if (newChars.isNotEmpty()) {
                    // 根据文本长度调整打字速度，用户要求减慢速度（单位：秒）
                    val speedInSeconds = when {
                        message.text.length < 50 -> 2.0 // 短文本每2秒显示一个字符
                        message.text.length < 150 -> 1.5 // 中等长度每1.5秒显示一个字符
                        else -> 1.0 // 长文本每1秒显示一个字符
                    }
                    
                    for (char in newChars) {
                        displayedText += char
                        delay((speedInSeconds * 10).toLong()) // 转换为毫秒
                    }
                    
                    // 当消息内容不再变化时，结束打字状态
                    if (displayedText == message.text) {
                        isTyping = false
                        viewModel.handleIntent(ChatIntent.UpdateisLoading(false))
                        // 发送打字状态更新给ViewModel
                        viewModel.handleIntent(ChatIntent.UpdateTypingStatus(message.messageId, isTyping = isTyping))
                    }
                }
            } else {
                // 历史AI消息，直接显示完整内容
                displayedText = message.text
                isTyping = false
                viewModel.handleIntent(ChatIntent.UpdateisLoading(false))
                // 发送打字状态更新给ViewModel
                viewModel.handleIntent(ChatIntent.UpdateTypingStatus(message.messageId, false))
            }
        }
    }

    Text(
        text = displayedText,
        modifier = Modifier.padding(12.dp),
        color = if (message.isFromUser) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSecondaryContainer
        },
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun ChatMessageBubble(message: ChatMessage, streamingMessageId: Long? = null, viewModel: ChatViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        // 获取屏幕宽度，让消息占更大比例
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxBubbleWidth = screenWidth * 0.9f // 消息最大宽度为屏幕宽度的80%
    
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
            modifier = Modifier.widthIn(max = maxBubbleWidth)
        ) {
            // 打字机效果实现，传递streamingMessageId参数
            AnimatedText(message = message, streamingMessageId = streamingMessageId, viewModel = viewModel )
        }
    }
}