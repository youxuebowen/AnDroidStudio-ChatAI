package com.bytedance.myapplication.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户信息实体类，代表数据库中的 'users' 表。
 */
@Entity(tableName = "users")
data class UserEntity(
    /**
     * 用户唯一标识符，设为主键。
     * 这里假设 userId 是从服务端获取的稳定ID，所以不自动生成。
     */
    @PrimaryKey
    val userId: String,

    /**
     * 用户昵称。
     */
    val nickname: String,

    /**
     * 用户头像的URL地址。
     * 可以为空，如果没有设置头像。
     */
    val avatarUrl: String?,

    /**
     * 账户创建时间的时间戳。
     */
    val creationTimestamp: Long = System.currentTimeMillis(),

    /*用户密码*/
    val userPassword: String
)
