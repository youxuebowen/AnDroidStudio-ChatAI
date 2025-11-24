package com.bytedance.myapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.util.Log
/*我要创建一个叫 AppDatabase 的数据库，它包含哪些表、版本号是多少、是否导出 schema”。*/
@Database(entities = [ChatSessionEntity::class, ChatMessageEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Log.d("AppDatabase", "开始创建数据库实例...")
                val instance = try {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "chat_history_database"
                    ).build()
                } catch (e: Exception){
                    Log.e("AppDatabase", "数据库创建失败！", e)
                    throw e
                }

                INSTANCE = instance
                Log.d("AppDatabase", "数据库实例创建成功：$instance")
                instance
            }
        }
    }
}