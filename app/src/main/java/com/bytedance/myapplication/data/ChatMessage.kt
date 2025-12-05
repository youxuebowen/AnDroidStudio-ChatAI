package com.bytedance.myapplication.data

import java.util.UUID

enum class MessageRole {
    System,
    User,
    Assistant
}

data class ChatMessage(
    val messageId: Long,
    val text: String,
    val isFromUser: Boolean, // 保留用于UI显示
    val role: MessageRole = if (isFromUser) MessageRole.User else MessageRole.Assistant,
    val timestamp: Long = System.currentTimeMillis(),
    val isTyping: Boolean = false, // 添加打字状态字段
) {
    // 转换为API需要的格式
    fun toApiMessage(): Map<String, String> {
        return mapOf(
            "role" to role.name.lowercase(),
            "content" to text
        )
    }
    
    companion object {
        fun fromApiMessage(role: String, content: String): ChatMessage {
            val messageRole = when (role.lowercase()) {
                "System" -> MessageRole.System
                "User" -> MessageRole.User
                "Assistant" -> MessageRole.Assistant
                else -> MessageRole.User
            }
            return ChatMessage(
                /*自动生成唯一 ID（UUID）——
                每个消息都需要一个唯一标识（比如用于列表刷新、去重），
                不用 API 返回或调用者手动生成，简化使用；*/
                messageId = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE,
                text = content,
                isFromUser = messageRole == MessageRole.User,
                role = messageRole,
                isTyping = false // 初始化打字状态为false
            )
        }
    }
}
