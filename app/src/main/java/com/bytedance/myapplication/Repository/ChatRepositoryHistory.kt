package com.bytedance.myapplication.Repository

import com.bytedance.myapplication.data.database.ChatDao
import com.bytedance.myapplication.data.database.ChatMessageEntity
import com.bytedance.myapplication.data.database.ChatSessionEntity
import kotlinx.coroutines.flow.Flow
import com.bytedance.myapplication.data.ChatMessage


class ChatRepositoryHistory(private val chatDao: ChatDao) {

    fun getAllSessions() = chatDao.getAllSessions()

    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>> {
        return chatDao.getMessagesForSession(sessionId)
    }

    suspend fun createNewSession(firstMessage: String, sessionId: Long): Long {
        val newSession = ChatSessionEntity(title = firstMessage,sessionId = sessionId)
        return chatDao.insertSession(newSession)
    }
    suspend fun deleteSession(sessionId: Long) {
        chatDao.deleteSession(sessionId)
    }


    suspend fun saveMessage(sessionId: Long, messages: List<ChatMessage>) {
        for (message in messages) {
            val entity = ChatMessageEntity(
//                id = 0,
                messageID = message.messageId,
                sessionId = sessionId,
//            text = message.text,
                text = message.text,
                sender = message.role,
            )
            chatDao.insertMessage(entity)
        }

    }
}