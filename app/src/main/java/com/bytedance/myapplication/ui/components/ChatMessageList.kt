package com.bytedance.myapplication.ui.components

import android.R.attr.padding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bytedance.myapplication.data.ChatMessage
import com.bytedance.myapplication.data.ChatSession
import kotlinx.coroutines.launch
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.padding
@Composable
fun ChatMessageList(
    messages: List<ChatMessage>,
    sessionId: Long? = null, // 可空，默认无选中
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    // 若当前无选中会话，显示提示
    if (sessionId == null) {
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
            Text(text = "欢迎使用SoulSoul")
        }
        return
    }

    // 自动滚动到底部（包括流式更新时）
    LaunchedEffect(messages.size, messages.lastOrNull()?.text) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(messages, key = { it.messageId }) { message ->
            ChatMessageBubble(message)
        }
    }
}
