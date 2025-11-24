package com.bytedance.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bytedance.myapplication.data.ChatSession

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
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
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

        Divider()

        // 会话列表
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
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