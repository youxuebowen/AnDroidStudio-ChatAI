package com.bytedance.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.navigation.compose.rememberNavController
import com.bytedance.myapplication.MVI.ChatIntent
import com.bytedance.myapplication.MVI.Screen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bytedance.myapplication.ui.theme.*
import com.bytedance.myapplication.ui.components.ProjectCard
import com.bytedance.myapplication.viewmodel.ArticleViewModel
import com.bytedance.myapplication.viewmodel.ChatViewModel
import com.bytedance.myapplication.ui.theme.BackgroundLight



@Composable
fun ProjectScreen(
    viewModel: ArticleViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel(),
    navController: androidx.navigation.NavController
) {
    // 获取ArticleViewModel实例
//    val viewModel: ArticleViewModel = viewModel()
    
    // 观察ViewModel中的projectState
    val projectState by viewModel.projectState.collectAsState()
    
    // 屏幕加载时获取文章列表
    LaunchedEffect(Unit) {
        viewModel.loadArticles()
    }

    Scaffold(
        topBar = { HeaderSection(count = projectState.projectList.size) { navController.navigate(Screen.Chat.route) } },
        containerColor =BackgroundLight// 白色背景
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            // 加载状态显示
            if (projectState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            } 
            // 错误状态显示
//            else if (projectState.errorMessage) {
//                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
//                    Text(text = projectState.errorMessage ?: "加载失败", color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
//                    Button(
//                        onClick = { viewModel.loadArticles() },
//                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
//                    ) {
//                        Text(text = "重试")
//                    }
//                }
//            }
            // 数据列表显示
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    if (projectState.projectList.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "暂无收藏项目")
                            }
                        }
                    } else {
                        items(projectState.projectList) {
                            ProjectCard(
                                item = it,
                                viewModel = viewModel,
                                onConfirmSelect = { content ->
                                    // 当用户确认选择后，更新ChatState的inputText
                                    chatViewModel.handleIntent(ChatIntent.UpdateInputText(content+ "你是专业的数据分析师，分析输入的内容"))
                                    // 导航回聊天页面
                                    navController.navigate(Screen.Chat.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// 顶部蓝色区域组件
@Composable
fun HeaderSection(count: Int, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OrangePrimary) // 顶部橙色背景
            .padding(start = 8.dp, end = 16.dp, top = 48.dp, bottom = 16.dp) // 适应状态栏高度
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 返回箭头
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 标题
            Text(
                text = "收藏项目",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 副标题 (项目数量)
        Text(
            text = "$count 个项目",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 44.dp) // 对齐标题文字
        )
    }
}