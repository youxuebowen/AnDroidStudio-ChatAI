package com.bytedance.myapplication.data

data class ChatSession(
    val sessionID: Long,
    val title: String,
//    val lastMessage: String,
    val startTime: Long,
    val messages: List<ChatMessage> = emptyList()
)