package com.bytedance.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bytedance.myapplication.data.ChatSession
import com.bytedance.myapplication.ui.theme.OrangePrimary

@Composable
fun DrawerContent(
    sessions: List<ChatSession>,
    currentSessionId: Long?,
    onSessionClick: (Long) -> Unit,
    onNewSessionClick: () -> Unit,
    onDeleteSession: (Long) -> Unit,
    modifier: Modifier = Modifier,
//    flashSessions: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
    ) {
        // 头部 - 新建对话按钮
        Surface(
            color = OrangePrimary,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "聊天历史",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                IconButton(onClick = onNewSessionClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "新建对话",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // 移除分割线，因为圆弧已经有良好的视觉分隔
        Spacer(modifier = Modifier.height(8.dp))

        // 会话列表，采用懒加载方式渲染会话列表，只渲染当前可见区域的会话项，提高性能
        LazyColumn(
            modifier = Modifier.weight(1f),
            //为列表添加垂直方向的8dp内边距，优化视觉效果
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            //遍历 sessions 列表，为每个会话创建一个 SessionItem 组件
            items(sessions, key = { it.sessionID }) { session ->
                SessionItem(
                    session = session,
                    isSelected = session.sessionID == currentSessionId,
                    onClick = { onSessionClick(session.sessionID) },
                    onDelete = { onDeleteSession(session.sessionID) }
                )
            }
        }
    }
}