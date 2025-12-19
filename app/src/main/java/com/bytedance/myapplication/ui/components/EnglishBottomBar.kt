package com.bytedance.myapplication.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bytedance.myapplication.MVI.Screen

// 底部栏组件
@Composable
fun EnglishBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Screen.Chat,
        Screen.English,
        Screen.Review
    )

    // 外层容器：确保在底部居中
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp), // 距离屏幕底部留一点间隙，浮空感更强
        contentAlignment = Alignment.Center
    ) {
        // 使用 Surface 来实现圆角、背景色和阴影
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.6f), // 保持你要求的 80% 宽度 (如果想用 0.6f 请自行修改)
            shape = RoundedCornerShape(percent = 50), // 设置为 50% 即左右完美半圆弧
            color = Color.White,
            shadowElevation = 8.dp, // 增加投影，使其更有立体感
            tonalElevation = 0.dp   // 设为 0 避免 Material3 自动给白色叠加上紫色色调
        ) {
            NavigationBar(
                // 必须清除 NavigationBar 默认的背景色，让外层 Surface 的颜色起作用
                containerColor = Color.Transparent,
                modifier = Modifier.height(64.dp) // 胶囊样式通常比普通底部栏稍窄一点
            ) {
                items.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(

                        icon = {
                            Box(
                                modifier = Modifier.fillMaxHeight(), // 充满父容器高度
//                                contentAlignment = Alignment.Center  // 内部垂直居中
                                contentAlignment = Alignment.BottomCenter // 1. 改为底部对齐
                            ) {
                                Icon(
                                    painter = painterResource(id = screen.iconResId),
                                    contentDescription = screen.label,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(bottom = 0.dp)
                                )
//                                Spacer(modifier = Modifier.height(6.dp))
//                                Text(
//                                    text = screen.label,
//                                    style = MaterialTheme.typography.labelSmall,
//                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
//                                )
                            }

                        },
                        label = {
                            Text(
                                text = screen.label,
                                style = MaterialTheme.typography.labelSmall // 文字改小一点更协调
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                onNavigate(screen.route)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = Color.Transparent, // 隐藏选中的那个圆形背景
                            // 修正：未选中时设为灰色，否则在白色底上白色图标会看不见
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    }
}