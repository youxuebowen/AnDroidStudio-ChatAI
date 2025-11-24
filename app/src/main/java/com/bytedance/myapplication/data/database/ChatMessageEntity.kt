package com.bytedance.myapplication.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bytedance.myapplication.data.MessageRole

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey( //定义了一个外键约束。
            entity = ChatSessionEntity::class, //指定这条消息关联的父表是 ChatSessionEntity（聊天会话实体）。
            parentColumns = ["sessionId"], //指定父表（ChatSessionEntity）中作为引用依据的列是其主键
            childColumns = ["sessionId"], //指定当前子表（chat_messages）中用于引用父表（ChatSessionEntity）的列是 sessionId。
            onDelete = ForeignKey.CASCADE //果父表中的一条记录（一个聊天会话）被删除，
        // 则所有引用了该父记录的外键记录（该会话下的所有聊天消息）也会被自动删除。这确保了数据库的数据一致性。
        )
    ],
    indices = [Index(value = ["sessionId"])] //在 sessionId 列上创建了一个索引。
)
data class ChatMessageEntity(
//    id 字段指定为该表的唯一标识符（主键）。
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0L ,
    @PrimaryKey
    val messageID: Long,
//    外键列。存储消息所属的聊天会话 ID，必须是非空。
    val sessionId: Long,
    val text: String,
    val sender: MessageRole, // e.g., "User", "AI", "System"
    val timestamp: Long = System.currentTimeMillis()
)