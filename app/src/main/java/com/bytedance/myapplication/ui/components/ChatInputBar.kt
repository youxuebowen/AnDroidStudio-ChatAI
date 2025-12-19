package com.bytedance.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun ChatInputBar(
    inputText: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {


    // 外部容器：通过 padding 实现“悬浮”在底部的效果
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp) // 悬浮边距
            .navigationBarsPadding() // 自动避开系统导航栏
            .padding(bottom = 20.dp),
//            .imePadding(), // 自动避开软键盘
        shape = CircleShape, // 胶囊形状
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp, // 增加阴影感
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp), // 内部小边距
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 使用 TextField 并隐藏指示线，使其完美嵌入胶囊
            TextField(
                value = inputText,
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp, max = 120.dp),
                placeholder = { Text("输入消息...") },
                maxLines = 4,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent, // 隐藏底部线条
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            // 发送按钮：保持圆形
            FilledIconButton(
                onClick = onSendClick,
                modifier = Modifier.size(48.dp),
                enabled = inputText.isNotBlank(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "发送",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}