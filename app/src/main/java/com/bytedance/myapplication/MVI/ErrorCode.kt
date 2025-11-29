package com.bytedance.myapplication.MVI

// ErrorCode.kt
sealed class ErrorCode(val code: Int, val message: String) {
    // 网络相关错误
    object NETWORK_ERROR : ErrorCode(1001, "网络连接失败")
    object TIMEOUT_ERROR : ErrorCode(1002, "请求超时")
    object SECURITY_PERMISSION_ERROR : ErrorCode(1003, "缺少网络权限")

    // API相关错误
    object API_UNAUTHORIZED : ErrorCode(2001, "未授权，请检查API密钥")
    object API_FORBIDDEN : ErrorCode(2002, "权限不足")
    object API_NOT_FOUND : ErrorCode(2003, "请求的资源不存在")
    object API_SERVER_ERROR : ErrorCode(2004, "服务器内部错误")

    // 数据处理错误
    object JSON_PARSE_ERROR : ErrorCode(3001, "JSON解析失败")
    object NULL_POINTER_ERROR : ErrorCode(3002, "空指针异常")

    // 数据库错误
    object DATABASE_ERROR : ErrorCode(4001, "数据库操作失败")
    object DATABASE_INIT_ERROR : ErrorCode(4002, "数据库未初始化")

    // 未知错误
    object UNKNOWN_ERROR : ErrorCode(9999, "未知错误")
}

// 扩展ChatApiException以支持错误码
class ChatApiException(
    val errorCode: ErrorCode,
    cause: Throwable? = null
) : Exception(errorCode.message, cause)