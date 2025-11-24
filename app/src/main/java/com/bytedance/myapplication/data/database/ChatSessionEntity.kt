package com.bytedance.myapplication.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/*@Entity: 告诉 Room 编译器，ChatMessageEntity 这个类对应数据库中的一张表。*/
@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0L,

    val title: String, // e.g., could be the first user message
    val startTime: Long = System.currentTimeMillis(),
    @PrimaryKey
    val sessionId: Long
)