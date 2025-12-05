package com.bytedance.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytedance.myapplication.data.ChatMessage
import com.bytedance.myapplication.data.ChatSession
import com.bytedance.myapplication.data.MessageRole
import com.bytedance.myapplication.MVI.ChatEffect
import com.bytedance.myapplication.MVI.ChatIntent
import com.bytedance.myapplication.MVI.ChatState
import com.bytedance.myapplication.Repository.ChatApiException
import com.bytedance.myapplication.Repository.ChatRepositoryAI
import com.bytedance.myapplication.Repository.ChatRepositoryHistory
import com.bytedance.myapplication.data.database.ChatMessageEntity
import android.database.sqlite.SQLiteException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bytedance.myapplication.data.database.ChatSessionEntity
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.collections.first
import kotlin.collections.map

class ChatViewModel (
    private val repositoryAi: ChatRepositoryAI,
    private val repositoryHistory: ChatRepositoryHistory,
    private var sessionId: Long? // Can be null for a new chat
): ViewModel() {

//    private val repository = ChatRepository()

    /*MutableStateFlow 就是一个可以被修改，并且能够将最新状态自动通知给所有观察者的数据容器。
    * 内部可变 (Mutable)*/
    private val _state = MutableStateFlow(ChatState())


    /*将它暴露为只读的 StateFlow（通过 asStateFlow() 方法），供 UI 层（如 Activity 或 Composable）订阅和观察
    * 状态封装，声明一个只读（immutable）的属性，命名为 state。
    *外部只读 (Immutable)*/
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ChatEffect>()
    val effect: SharedFlow<ChatEffect> = _effect.asSharedFlow()

    init {
        // 当 ChatViewModel 对象创建后，立即执行以下代码
        /*用于初始化一些session，后期可以删除*/
//        loadInitialData()
        sessionId?.let {
            loadChatHistory(it)
            getSessions()
        }
    }

    private fun loadChatHistory(id: Long) {
        repositoryHistory.getMessagesForSession(id)
            .onEach { entities ->
                val messages = entities.map { it.toChatMessage() }
                _state.update { it.copy(currentMessages = messages) }
            }
            .launchIn(viewModelScope)
    }

    /*返回*/
    private fun ChatMessageEntity.toChatMessage(): ChatMessage {
        return ChatMessage(
            messageId = this.messageID,
            text = this.text,
            isFromUser = MessageRole.User == this.sender,
            timestamp = this.timestamp,
            role = this.sender,
            isTyping = false // 从数据库加载的消息默认不是打字状态
        )
    }
    
    private fun updateTypingStatus(messageId: Long, isTyping: Boolean) {
        _state.value = _state.value.copy(
            currentMessages = _state.value.currentMessages.map { msg ->
                if (msg.messageId == messageId) {
                    msg.copy(isTyping = isTyping)
                } else {
                    msg
                }
            }
        )
    }
    private fun updateIsLoading(isLoading: Boolean){
        _state.value = _state.value.copy(
            isLoading = isLoading
        )
    }



    //UI发送Intent是执行相应逻辑
    fun handleIntent(intent: ChatIntent) {
        /*它接收一个名为 intent 的参数，类型是 ChatIntent*/
        /*它定义了一个函数 handleIntent，这个函数接收一个用户意图（ChatIntent）作为输入，
        并根据意图的类型（通过 when 表达式）执行相应的业务逻辑。*/
        when (intent) {
            /*这是一个类型检查（Type Check）。它检查传入的 intent 实例是否是 ChatIntent 密封类层次结构中的 SendMessage 这个子类的实例。
            * 如果左侧条件成立，则执行右侧的代码”
            * 一旦 intent 被确认是 ChatIntent.SendMessage 类型，
            * Kotlin 编译器会自动将其视为该类型，因此你可以直接访问其特定的属性，例如 intent.text*/
            is ChatIntent.SendMessage -> sendMessage(intent.text)
            is ChatIntent.GetSessions -> getSessions()
            is ChatIntent.GetMessagesForSession -> getMessagesForSession(intent.sessionId)
            is ChatIntent.GetMessages -> getMessagesForSession(intent.sessionId)
            /*在确定是UpdateInputText这个意图后，下面定义updateInputText这个函数执行这个逻辑*/
            is ChatIntent.UpdateInputText -> updateInputText(intent.text)
            is ChatIntent.SelectSession -> selectSession(intent.sessionId) //选中历史会话，需要历史会话的ID
            is ChatIntent.CreateNewSession -> createNewSession()
            is ChatIntent.DeleteSession -> deleteSession(intent.sessionId) //删除会话，需要绘画的ID
            is ChatIntent.ToggleDrawer -> toggleDrawer() //打开侧边栏
            is ChatIntent.UpdateTypingStatus -> updateTypingStatus(intent.messageId, intent.isTyping)
            is ChatIntent.UpdateisLoading -> updateIsLoading(intent.isLoading)
            else -> {}
        }
    }

    private fun getSessions(){

            repositoryHistory.getAllSessions().onEach { entities ->
                val session = entities.map { it.toChatSession() }
                _state.update { it.copy(sessions = session) }
            }.launchIn(viewModelScope)
        /*不托管，是收集不到的*/
//        最后加了这个成功了，将 Flow 数据流的收集工作，“托管” 给 ViewModel 的协程作用域（viewModelScope）执行

    }

    private fun ChatSessionEntity.toChatSession(): ChatSession {
        return ChatSession(
            sessionID = this.sessionId,
            title = this.title,
//            lastMessage = this.lastMessage,
            startTime = this.startTime,
            messages = _state.value.currentMessages
        )
    }
    private fun getMessagesForSession(sessionId: Long){


                 repositoryHistory.getMessagesForSession(sessionId=sessionId).onEach {
                     entities ->
                     val messages = entities.map { it.toChatMessage() }
                     _state.update { it.copy(currentMessages = messages) }

                 }.launchIn(viewModelScope)


                _state.value = _state.value.copy(
                    currentSessionId=sessionId
                )
//                _state.update { it.copy(currentSessionId = sessionId) }


    }

    private fun selectSession(sessionId: Long) {
        viewModelScope.launch {
            try {
                repositoryHistory.getMessagesForSession(sessionId=sessionId).onEach {
                        entities ->
                    val messages = entities.map { it.toChatMessage() }
                    _state.update { it.copy(currentMessages = messages) }

                }
                _state.update { it.copy(currentSessionId = sessionId) }
            }catch (e:Exception){
                _effect.emit(ChatEffect.ShowToast(e.message.toString()))
            }
        }
        /*val session = _state.value.sessions.find { it.sessionID == sessionId }
        session?.let {
            _state.value = _state.value.copy(
                currentSessionId = it.sessionID,
                currentMessages = it.messages,
                isDrawerOpen = false
            )
        }*/
    }
    private fun sendMessage(text: String) {
        if (text.isBlank()) {
            viewModelScope.launch {
                _effect.emit(ChatEffect.ShowToast("消息不能为空"))
            }
            return
        }

        // 如果正在加载中，忽略新消息，_state是可以在内部更改的
        if (_state.value.isLoading) {
            return
        }
//        刚开始是null
        var sessionId = _state.value.currentSessionId

        if (sessionId == null) {
            createNewSession()
            /*上一步已经创建session，所以不可能为空*/
            /*!! 操作符用于强制告诉编译器：“我知道这个值是可空的，但请相信我，在代码运行到这里时，它绝对不会是 null。”*/
            sessionId = _state.value.currentSessionId!!
        }

        // 1. 添加用户消息到历史记录
        val userMessage = ChatMessage(
            messageId = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE,
            text = text,
            isFromUser = true,
            role = MessageRole.User
        )
//        viewModelScope.launch {
//            // 保存消息
//
//
//        }


        /*当您在 Kotlin 中对一个集合（如 List）使用 + 操作符时，它实际上调用了一个隐藏的扩展函数，该函数的行为是：
        不修改原列表（不可变性）：_state.value.currentMessages 这个原始列表不会被修改。
        创建新列表：它返回一个全新的 List 实例。
        内容组合：这个新列表包含：
        原始列表 (_state.value.currentMessages) 中的所有元素。
        紧接着是 + 号右侧的元素 (userMessage)。*/
        val updatedMessages = _state.value.currentMessages + userMessage
        _state.value = _state.value.copy(
            currentMessages = updatedMessages,
            inputText = "",
            isLoading = true
        )

        updateSessionMessages(sessionId, updatedMessages)

        // 2. 创建assistant消息占位符（用于流式更新）
        val assistantMessageId = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
        _state.value = _state.value.copy(streamingMessageId=assistantMessageId)
        val AssistantMessage = ChatMessage(
            messageId = assistantMessageId,
            text = "",
            isFromUser = false,
            role = MessageRole.Assistant
        )

        val messagesWithPlaceholder = updatedMessages + AssistantMessage
        _state.value = _state.value.copy(
            currentMessages = messagesWithPlaceholder,
//            streamingMessageId = assistantMessageId,
            streamingContent = ""
        )

        // 3. 调用API并接收流式响应
        /*“在 ViewModel 的生命周期内，启动一个不会阻塞主线程的异步任务，并在 ViewModel 被销毁时自动停止这个任务。”
        * 它将协程的生命周期绑定到 ViewModel 的生命周期
        * .launch (协程构建器)启动它只是为了执行操作（如保存数据、发送网络请求），而不需要立即返回结果。*/
        viewModelScope.launch {
            try {
//                sessionId?.let { repositoryHistory.saveMessage(sessionId, Message.User(text=text)) }
                repositoryAi.streamChat(
                    messages = updatedMessages, // 发送历史消息（包含刚添加的用户消息）
                ).collect { token ->
                    /*collect会暂停当前协程，直到 streamChat 流发出第一个值。然后，它会为流中发出的每一个 token 执行 {} 中的代码块。
                    token: 每次循环接收到的单个字符串片段（例如一个字或一个词）。*/
                    //断点6：在这里设置断点，查看ViewModel接收到的每个token
                    // 在Debugger中查看：
                    //   - token: 单个token字符串
                    
                    // 4. 实时更新流式内容，实现打字效果
                    _state.value = _state.value.copy(
                        // 5. 直接将token添加到当前消息中，实现打字效果
                        currentMessages = _state.value.currentMessages.map { msg ->
                            if (msg.messageId == assistantMessageId) {
                                // 将新收到的token直接添加到现有消息内容的末尾
                                msg.copy(text = msg.text + token)
                            } else {
                                msg
                            }
                        }
                    )
                    
                    // 记录token信息
                    val currentAssistantMessage = _state.value.currentMessages.find { it.messageId == assistantMessageId }
                    Log.d(
                        "ChatViewModel",
                        "收到Token: '$token', 当前消息长度: ${currentAssistantMessage?.text?.length ?: 0}"
                    )
                }

                // 6. 流式接收完成，保存最终消息
                // 从当前消息列表中获取完整的助手消息内容
                val assistantMessageContent = _state.value.currentMessages.find { 
                    it.messageId == assistantMessageId 
                }?.text ?: ""
                
                val finalAssistantMessage = ChatMessage(
                    messageId = assistantMessageId,
                    text = assistantMessageContent,
                    isFromUser = false,
                    role = MessageRole.Assistant
                )
                val finalMessages = updatedMessages + finalAssistantMessage
//                var isTyping by remember(message.messageId) {
//                    mutableStateOf(false)
//                }
//                when(){
//
//                }
                val lastMessage = _state.value.currentMessages.lastOrNull()
                lastMessage?.isTyping?.let {
                    if (!it){
                        _state.value = _state.value.copy(
                            currentMessages = finalMessages,
                            isLoading = false,
                            streamingMessageId = null,
                            streamingContent = ""
                        )
                    }
                }
//                _state.value = _state.value.copy(
//                    currentMessages = finalMessages,
//                    isLoading = false,
//                    streamingMessageId = null,
//                    streamingContent = ""
//                )



                // 7. 更新会话消息（持久化存储）
                updateSessionMessages(sessionId, finalMessages)

                saveMessage(sessionId, finalMessages)



            } catch (e: ChatApiException) {
                // API调用失败
                e.printStackTrace() // 打印堆栈信息便于调试
                viewModelScope.launch {
                    _effect.emit(ChatEffect.ShowToast("发送失败: ${e.message}"))
                }

                // 移除占位符消息
                _state.value = _state.value.copy(
                    currentMessages = updatedMessages,
                    isLoading = false,
                    streamingMessageId = null,
                    streamingContent = ""
                )
            } catch (e: Exception) {
                // 其他异常（网络异常、解析异常等）
                // 记录详细的错误信息到Logcat
                Log.e("ChatViewModel", "发送消息时发生异常", e)
                Log.e("ChatViewModel", "异常类型: ${e.javaClass.simpleName}")
                Log.e("ChatViewModel", "异常消息: ${e.message}")
                Log.e("ChatViewModel", "堆栈跟踪:", e)
                e.printStackTrace()

                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true -> "网络连接失败，请检查网络"
                    e.message?.contains("timeout") == true -> "请求超时，请重试"
                    e.message?.contains("SecurityException") == true -> "缺少网络权限，请检查AndroidManifest.xml"
                    e.message?.contains("NullPointerException") == true -> "空指针异常，请查看Logcat详情"
                    e.message?.contains("JsonSyntaxException") == true -> "JSON解析失败，API返回格式可能不匹配"
                    else -> "发生错误: ${e.message ?: e.javaClass.simpleName}，请查看Logcat"
                }
                viewModelScope.launch {
                    _effect.emit(ChatEffect.ShowToast(errorMsg))
                }

                _state.value = _state.value.copy(
                    currentMessages = updatedMessages,
                    isLoading = false,
                    streamingMessageId = null,
                    streamingContent = ""
                )
            }
        }
    }

    private fun updateInputText(text: String) {
        _state.value = _state.value.copy(inputText = text)
    }



    private fun createNewSession() {
        val sessionID = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
        val newSession = ChatSession(
//            id = UUID.randomUUID().toString(),
            sessionID = sessionID,
            title = "新对话",
//            lastMessage = "",
            startTime = System.currentTimeMillis(),
            messages = emptyList()
        )

        _state.value = _state.value.copy(
            sessions = listOf(newSession) + _state.value.sessions,
            currentSessionId = newSession.sessionID,
            currentMessages = emptyList(),
            isDrawerOpen = false
        )
    }

    private fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            try {
                repositoryHistory.deleteSession(
                    sessionId = sessionId
                )
            }catch (e:Exception){
                // 1. 打印异常核心信息到 Logcat
                Log.e("ChatViewModel_SaveError", "异常类型：${e.javaClass.simpleName}") // 异常类型（如 SQLExeption）
                Log.e("ChatViewModel_SaveError", "异常消息：${e.message ?: "无详细消息"}") // 异常描述（如“表不存在”）
                Log.e("ChatViewModel_SaveError", "异常堆栈：", e) // 完整堆栈（关键！能定位到具体报错代码行）

                // 2. 给用户友好提示（可根据异常类型定制）
                val errorTip = when (e) {
                    is SQLiteException -> "数据库操作失败，请检查表结构"
                    is NullPointerException -> "参数为空，请重试"
                    is IllegalStateException -> "数据库未初始化"
                    else -> "聊天记录保存失败，请稍后重试"
                }
                _effect.emit(ChatEffect.ShowToast(errorTip))
            }
        }
    }

    private fun toggleDrawer() {
        _state.value = _state.value.copy(isDrawerOpen = !_state.value.isDrawerOpen)
    }

    private fun updateSessionMessages(sessionId: Long, messages: List<ChatMessage>) {
        val updatedSessions = _state.value.sessions.map { session ->
            if (session.sessionID == sessionId) {
                session.copy(
                    messages = messages,
//                    lastMessage = messages.lastOrNull()?.text ?: "",
                    startTime = System.currentTimeMillis(),
                    title = if (session.title == "新对话" && messages.isNotEmpty()) {
                        messages.first().text.take(20)
                    } else {
                        session.title
                    }
                )
            } else {
                session
            }
        }
        _state.value = _state.value.copy(sessions = updatedSessions)
    }

    private fun saveMessage(sessionId: Long, messages: List<ChatMessage>) {
        viewModelScope.launch {
            try {
                repositoryHistory.createNewSession(
                    firstMessage = messages.first().text,
                    sessionId = sessionId
                )
                repositoryHistory.saveMessage(sessionId, messages)
            }catch (e:Exception){
                // 1. 打印异常核心信息到 Logcat
                Log.e("ChatViewModel_SaveError", "异常类型：${e.javaClass.simpleName}") // 异常类型（如 SQLExeption）
                Log.e("ChatViewModel_SaveError", "异常消息：${e.message ?: "无详细消息"}") // 异常描述（如“表不存在”）
                Log.e("ChatViewModel_SaveError", "异常堆栈：", e) // 完整堆栈（关键！能定位到具体报错代码行）

                // 2. 给用户友好提示（可根据异常类型定制）
                val errorTip = when (e) {
                    is SQLiteException -> "数据库操作失败，请检查表结构"
                    is NullPointerException -> "参数为空，请重试"
                    is IllegalStateException -> "数据库未初始化"
                    else -> "聊天记录保存失败，请稍后重试"
                }
                _effect.emit(ChatEffect.ShowToast(errorTip))
            }
        }
//        viewModelScope.launch {
//)
//        repositoryHistory.saveMessage(sessionId, messages)
    }
}
    class ChatViewModelFactory(
        private val repositoryAI: ChatRepositoryAI,
        private val repositoryHistory: ChatRepositoryHistory,
        private val sessionId: Long?
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(com.bytedance.myapplication.viewmodel.ChatViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ChatViewModel(
                    repositoryAi = repositoryAI,
                    repositoryHistory = repositoryHistory,
                    sessionId = sessionId
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

