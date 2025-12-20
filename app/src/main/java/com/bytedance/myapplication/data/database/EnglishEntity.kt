package com.bytedance.myapplication.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recognition_history")
data class EnglishEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val wordEn: String,          // 英文单词
    val wordCn: String,          // 中文意思
    val imagePath: String,       // 图片本地路径
    val audioPath: String?,      // 语音本地路径（可能有延迟，允许为空）
    val timestamp: Long = System.currentTimeMillis()
)
