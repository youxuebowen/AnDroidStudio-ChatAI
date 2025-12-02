package com.bytedance.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytedance.myapplication.ui.theme.OrangePrimary
import com.bytedance.myapplication.R

@Composable
fun OnboardingScreen(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp,    // 左上角：直角
                    topEnd = 0.dp,      // 右上角：直角
                    bottomEnd = 60.dp,  // 右下角：60dp圆角
                    bottomStart = 60.dp // 左下角：60dp圆角
                )
            ),
    ) {
        // --- 顶部图片区域 (约占 50%-60% 高度) ---
        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(0.6f)
//                .background(OrangePrimary) // 背景色为橙色
//                .clip(MaterialTheme.shapes.large), // 设置为圆弧形
//            contentAlignment = Alignment.Center
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(OrangePrimary)
                // 自定义圆角大小（比如 20dp）
                .clip(RoundedCornerShape(60.dp))
            // 进阶：设置不同方向的圆角（比如仅左上和右下圆角）
            // .clip(RoundedCornerShape(topStart = 30.dp, bottomEnd = 30.dp))
            ,
            contentAlignment = Alignment.Center
        ) {
            // 模拟图片：网球运动员和熊猫
            // 您需要替换成实际的 drawable 资源
             Image(
                 painter = painterResource(id = R.drawable.loading),
                 contentDescription = "Onboarding illustration",
                 modifier = Modifier.size(width = 240.dp, height = 289.dp),
                 contentScale = ContentScale.Crop

             )

            /*// 占位符 Box 来模拟图片区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                // 假设这是放置熊猫和网球运动员图片的位置
                Text(
                    text = "[Onboarding Graphic Placeholder]",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }*/
        }

        // --- 底部文字和按钮区域 (约占 40%-50% 高度) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .padding(horizontal = 32.dp, vertical = 24.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 标题
            Text(
                text = "Hello,I'm your Assistant.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 副标题
            Text(
                text = "Working with you.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 登录按钮
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = MaterialTheme.shapes.extraSmall // 模拟圆角
            ) {
                Text(
                    text = "Log in",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}