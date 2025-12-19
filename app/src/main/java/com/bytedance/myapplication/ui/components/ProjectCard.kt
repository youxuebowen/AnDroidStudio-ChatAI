package com.bytedance.myapplication.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable
import com.bytedance.myapplication.Network.Article
import com.bytedance.myapplication.viewmodel.ArticleViewModel
// 数据模型：仅包含标题和URL
@SuppressLint("UnsafeOptInUsageError")


// 组件：简化的项目卡片
@Composable
fun ProjectCard(item: Article, viewModel: ArticleViewModel, onConfirmSelect: (String) -> Unit) {
    val context = LocalContext.current
    // 从ViewModel获取选中状态
    val projectState by viewModel.projectState.collectAsState()
    // 判断当前文章是否被选中

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // 轻微阴影
        shape = RoundedCornerShape(12.dp) // 圆角
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp) // 内部边距
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧信息区域
            Column(modifier = Modifier.weight(1f)) {
                // 标题
                Text(
                    text = item.name as String,
                    fontSize = 18.sp,
//                    maxLines = 5,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // URL 显示 (灰色，单行，过长省略)
                val contentText = item.content as? String
                if (!contentText.isNullOrEmpty()) {
                    Text(
                        text = contentText,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 10,
                        overflow = TextOverflow.Ellipsis
                    )
                }

//                Spacer(modifier = Modifier.height(6.dp))
//                // URL 显示 (灰色，单行，过长省略)
//                Text(
//                    text = item.url as String,
//                    fontSize = 14.sp,
//                    color = Color.Gray,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
            }

            // 添加Checkbox选项框
            Checkbox(
                checked = projectState.isChecked,
                onCheckedChange = { newValue ->
                    // 将状态管理移交给ViewModel
                    viewModel.onArticleChecked(item, newValue)
                },
                modifier = Modifier.padding(end = 16.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF5C6BC0),
                    checkmarkColor = Color.White
                )
            )

            // 右侧装饰：箭头图标 (提示可点击)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open Link",
                tint = Color(0xFF5C6BC0), // 保持和之前一致的蓝色主题
                modifier = Modifier.clickable {
                    // 点击图标调用系统浏览器打开 URL
                    val isUrlValid = item.url?.isNotEmpty() ?: false
                    if (isUrlValid) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace() // 防止无效URL导致崩溃
                        }
                    }
                }
            )
        }
    }
    
    // 确认对话框
    if (projectState.showDialog && projectState.selectedArticleId == item.name) {
        AlertDialog(
            onDismissRequest = {
                // 取消时通过ViewModel重置状态
                viewModel.dismissDialog()
            },
            title = { Text(text = "确认操作") },
            text = { Text(text = "发送至AI助手，帮您分析！") },
            confirmButton = {
                Button(
                    onClick = {
                        // 确认选中并获取URL
                        val content = viewModel.confirmSelection()
                        if (content != null) {
                            // 回调通知上层组件处理URL
                            onConfirmSelect(content)
                        }
                    }
                ) {
                    Text(text = "确认")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // 取消时通过ViewModel重置状态
                        viewModel.dismissDialog()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text(text = "取消")
                }
            }
        )
    }
}