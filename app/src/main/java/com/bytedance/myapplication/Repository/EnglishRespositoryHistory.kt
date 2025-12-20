package com.bytedance.myapplication.Repository

import com.bytedance.myapplication.data.database.ChatDao
import com.bytedance.myapplication.data.database.ChatMessageEntity
import com.bytedance.myapplication.data.database.EnglishDao
import com.bytedance.myapplication.data.database.EnglishEntity
import kotlinx.coroutines.flow.Flow

class EnglishRespositoryHistory(private val englishDao: EnglishDao) {
//    返回的id自增
    suspend fun insertEnglish(english: EnglishEntity): Long{
        return englishDao.insert(english)
    }
    suspend fun updateAudioPath(id: Long, audioPath: String){
        return  englishDao.updateAudioPath(id, audioPath)
    }
    fun getAllHistory() = englishDao.getAllHistory()

}