package com.bytedance.myapplication.Network

// SSE流式响应数据类
data class ChatStreamChunk(
    val id: String? = null,
    val objectType: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<StreamChoice>? = null
)

data class StreamChoice(
    val index: Int? = null,
    val delta: StreamDelta? = null,
    val finishReason: String? = null
)

data class StreamDelta(
    val role: String? = null,
    val content: String? = null
)



