package com.bytedance.myapplication.Network

data class ChatApiRequest(
    val model: String,
    val messages: List<Map<String, String>>,
    val stream: Boolean = true,
//    val Authorization: String
)



