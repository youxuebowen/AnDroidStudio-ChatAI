package com.bytedance.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.bytedance.myapplication.MVI.ChatEffect
import com.bytedance.myapplication.MVI.ChatIntent
import com.bytedance.myapplication.MVI.Screen
import com.bytedance.myapplication.ui.components.*
import com.bytedance.myapplication.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    navController: androidx.navigation.NavController
) {
    /*
    从 ViewModel 里实时收集 UI 状态（state 是 StateFlow）。
    state 里面通常包含：
    messages: List<Message>      // 聊天记录
    inputText: String            // 当前输入框的内容
    isLoading: Boolean           // 是否正在发送/接收消息等
    */
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    LaunchedEffect(state.isDrawerOpen) {
        if (state.isDrawerOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }
    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Closed && state.isDrawerOpen) {
            viewModel.handleIntent(ChatIntent.ToggleDrawer)
        }
    }
    // 处理Effect (单次事件)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ChatEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
//                    flashSessions = {
//                        viewModel.handleIntent(ChatIntent.GetSessions)
//                    },
                    sessions = state.sessions,
                    currentSessionId = state.currentSessionId,
                    onSessionClick = {
                        viewModel.handleIntent(ChatIntent.SelectSession(it))
                        scope.launch {
                            drawerState.close()
                        }

                        viewModel.handleIntent(ChatIntent.GetMessages(it))
                    },
                    onNewSessionClick = {
                        viewModel.handleIntent(ChatIntent.CreateNewSession)
                    },
                    onDeleteSession = {
                        viewModel.handleIntent(ChatIntent.DeleteSession(it))
                    }
                )
            }
        }
    ){
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                ChatTopBar(
                    onMenuClick = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                viewModel.handleIntent(ChatIntent.GetSessions)
                                drawerState.open()
                            }
                            else drawerState.close()

                        }
                    },
                    onInfoClick = {
                        navController.navigate(Screen.Project.route)
                    },
                    onEnglishClick = {
                        navController.navigate(Screen.English.route)
                    }
                )


            },
            bottomBar = {
                ChatInputBar(
                    inputText = state.inputText,
                    onTextChange = {
                        viewModel.handleIntent(ChatIntent.UpdateInputText(it))
                    },
                    onSendClick = {
                        viewModel.handleIntent(ChatIntent.SendMessage(state.inputText))
                    }
                )
            }
        ) { paddingValues ->
            ChatMessageList(
                messages = state.currentMessages,
                sessionId = state.currentSessionId,
                streamingMessageId = state.streamingMessageId,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}
/*在页面主内容区显示聊天消息列表（ChatMessageList），
并通过 paddingValues 自动适配 Scaffold 的顶部栏、底部栏，避免列表被遮挡。*/

//    小括号是传参，大括号是主要内容
