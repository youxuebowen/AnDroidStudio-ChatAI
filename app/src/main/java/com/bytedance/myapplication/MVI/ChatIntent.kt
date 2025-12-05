package com.bytedance.myapplication.MVI

// 推荐写法：sealed class（MVI 架构标准）
/*主要用于区分不同的Intent，以及传参最后逻辑在ViewModel里执行*/
sealed class ChatIntent {
    /*使得你在处理这些意图时可以使用穷尽性的 when 表达式，从而提高代码的健壮性。*/
    /*声明一个 data class 作为 ChatIntent 的一个子类。：是继承；
    * val text:String，这个意图携带的数据。SendMessage 意图需要知道用户想要发送的文本内容。*/
    /*发送消息。不执行任何操作，只是获取文本，实际上都一样，只是通过命名区分开来，来定义不同的子类，来区分
    * 用户执行的什么操作，并把数据传给ViewModel*/
    data class SendMessage(val text:String) : ChatIntent()
    object GetSessions : ChatIntent()
    data class GetMessagesForSession(val sessionId:Long) : ChatIntent()
    data class GetMessages(val sessionId:Long):ChatIntent()
    /*更新输入框文本*/
    data class UpdateInputText(val text: String) : ChatIntent()
    // 新增以下Intent。选择一个聊天会话
    data class SelectSession(val sessionId: Long) : ChatIntent()
    /*创建新会话*/
    object CreateNewSession : ChatIntent()
    /*删除一个聊天会话*/
    data class DeleteSession(val sessionId: Long) : ChatIntent()
    object ToggleDrawer : ChatIntent() //切换抽屉菜单
    // 更新消息的打字状态
    data class UpdateTypingStatus(val messageId: Long, val isTyping: Boolean) : ChatIntent()
    data class UpdateisLoading(val isLoading: Boolean) : ChatIntent()
    // 你以后还可以继续加
//    data object LoadHistory : ChatIntent()          // 无参数意图
//    data class DeleteMessage(val id: Long) : ChatIntent()
}