package com.bytedance.myapplication.MVI
import com.bytedance.myapplication.data.ChatMessage
import com.bytedance.myapplication.data.ChatSession
/*
* 专门用来「集中管理聊天页面的所有 UI 相关状态」
* 页面上能看到的（比如消息列表、输入框文字）、
* 用户能感知的（比如加载中、侧边栏是否打开）、
* 业务需要的（比如当前会话 ID、流式消息内容）状态，都统一存在这里。*/

/*data class 是 Kotlin 专门用来「存储数据」的类，
会自动生成 equals()、hashCode()、toString() 等方法，
还支持解构赋值 —— 非常适合作为 “状态容器”（只存数据，不写复杂逻辑）。*/

/*ViewModel 不会修改原有 ChatState 对象，
而是创建一个新的 ChatState 实例（用 copy() 方法）来更新状态，
确保状态变化可追溯、不混乱。*/

/*这个 ChatState 是「不可变状态」（所有字段都是 val）*/
data class ChatState(
    /*ChatSession自定义的数据类*/
    val sessions: List<ChatSession> = emptyList(),        // 新增
    /*当前选中的会话 ID —— 用来关联「当前显示的消息列表属于哪个会话」（比如切换会话时，更新 currentMessages）。*/
    val currentSessionId: Long? = null,                  // 新增
    val currentMessages: List<ChatMessage> = emptyList(), // 替换原messages
//    val currentMessage: ChatMessage, // 当前单个消息
    val inputText: String = "",
    /*比如发送消息后等待 AI 回复时，显示 “加载中” 动画（true 显示，false 隐藏）。*/
    val isLoading: Boolean = false,
    /*制侧边栏的显示 / 隐藏（true 打开，false 关闭）。*/
    val isDrawerOpen: Boolean = false,                      // 新增
    val streamingMessageId: Long? = null,                // 正在流式接收的消息ID
    val streamingContent: String = ""  ,
    val isTyping: Boolean = false
    // 正在流式接收的内容（临时）
)
/*新增的 sessions 和 currentSessionId 是为了实现 “多会话切换”
（比如用户可以创建多个聊天窗口，切换不同的对话），
这是聊天类 App 的常见需求，原来的单一 messages 无法满足。*/