package com.bytedance.myapplication.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bytedance.myapplication.data.ChatMessage

import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import com.bytedance.myapplication.R

@Composable
fun ChatMessageList(
    messages: List<ChatMessage>,
    sessionId: Long? = null, // 可空，默认无选中
    modifier: Modifier = Modifier,
//    viewModel: ChatViewModel
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    // 若当前无选中会话，显示提示
    if (messages.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = PaddingValues(
                    start = 16.dp,    // 左边距
                    top = 24.dp,     // 上边距
                    end = 16.dp,     // 右边距
                    bottom = 24.dp   // 下边距
                )),
            contentAlignment = Alignment.Center
        ) {
            // 垂直排列图片和文本，保持居中
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 添加icon_chat图片
                Image(
                    painter = painterResource(id = R.drawable.icon_chat),
                    contentDescription = "Chat icon",
                    modifier = Modifier
                        .padding(bottom = 8.dp) // 图片与文字间距
                        .size(64.dp)            // 设置图片大小为 64dp
                )
                // 原有的文本组件
                Text(
                    text = buildAnnotatedString {
                        append("What's ")
                        withStyle(style = SpanStyle(color = Color(0xFF9C27B0))) {
                            append("your mind")
                        }
                        append("?")
                    },
                    fontSize = 24.sp // 设置字体大小
                )
            }
        }
        return
    }

    // 自动滚动到底部（包括流式更新时和键盘弹出时）
    LaunchedEffect(messages.size, messages.lastOrNull()?.text) {
        if (messages.isNotEmpty()) {
            // 使用延迟确保UI更新完成后再滚动
            delay(16) // 16ms约等于一帧的时间
            try {
                // 直接滚动到最后一项，不使用动画以提高性能
                listState.scrollToItem(messages.size - 1)
            } catch (e: Exception) {
                // 忽略滚动异常，避免影响UI
            }
        }
    }
    
    // 使用DisposableEffect确保在Composable销毁时不会有资源泄漏
    DisposableEffect(listState, messages) {
        // 使用rememberCoroutineScope创建的协程作用域，而不是直接使用viewModelScope
        val scrollJob = coroutineScope.launch {
            try {
                if (messages.isNotEmpty()) {
                    listState.scrollToItem(messages.size - 1)
                }
            } catch (e: Exception) {
                // 忽略异常
            }
        }
        
        // 提供清理函数，在Composable销毁时执行
        onDispose {
            scrollJob.cancel()
        }
    }
    
    // 确保消息列表在底部留出足够空间，避免被输入框遮挡
    val bottomPadding = 32.dp // 固定的底部内边距，确保足够空间

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        state = listState,
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = bottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(messages, key = { it.messageId }) { message ->
            ChatMessageBubble(message)
        }
    }
}
