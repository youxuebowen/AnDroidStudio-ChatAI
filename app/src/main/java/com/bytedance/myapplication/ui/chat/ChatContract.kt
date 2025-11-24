package com.bytedance.myapplication.ui.chat

// com/bytedance/myapplication/ui/chat/ChatContract.kt


// 消息类型，用于适配聊天气泡的样式
//sealed class Message {
//    data class User(val text: String) : Message() // 用户消息
//    data class AI(val text: String) : Message() // AI 文本消息
//    data class System(val text: String) : Message() // 系统提示或引导词
//    data class Image(val imageUrl: String) : Message() // 假设用于未来展示图片
//}

//// 定义 UI 状态 (Model)
//data class ChatState(
//    val messages: List<Message> = emptyList(), // 聊天消息列表
//    val inputMessage: String = "" // 输入框当前内容
//)
//
//// 定义用户意图 (Intent)
//sealed class ChatIntent {
//    data class UpdateInputMessage(val newMessage: String) : ChatIntent() // 更新输入框
//    data object SendMessage : ChatIntent() // 发送当前输入的消息
//    data object GenerateImage : ChatIntent() // 点击“帮我生成图片”按钮
//}