package com.bytedance.myapplication.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
//数据库访问对象
@Dao
interface EnglishDao {
    @Insert
    suspend fun insert(record: EnglishEntity): Long

    @Query("UPDATE recognition_history SET audioPath = :audioPath WHERE id = :id")
    suspend fun updateAudioPath(id: Long, audioPath: String)

    @Query("SELECT * FROM recognition_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<EnglishEntity>>
}
