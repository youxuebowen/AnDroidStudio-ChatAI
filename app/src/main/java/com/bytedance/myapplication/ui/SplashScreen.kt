package com.bytedance.myapplication.ui
import com.bytedance.myapplication.viewmodel.SplashViewModel
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.bytedance.myapplication.MVI.SplashIntent
import androidx.compose.foundation.layout.Column
@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onFinished: () -> Unit,
    logoResId: Int? = null // 如果你以后想放图片 logo，仍然可以传
) {
    val state by viewModel.state.collectAsState()

    // 入场动画：淡入 + 轻微放大
    var startAnimation by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.95f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        viewModel.dispatch(SplashIntent.StartTimer)
    }

    LaunchedEffect(state.loading) {
        if (!state.loading) {
            delay(500L) // 退出前稍作停留，让动画更自然
            onFinished()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7)),
            contentAlignment = Alignment.Center
        ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center, // 垂直居中
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alpha)
                        .scale(scale)
                ) {
                    // 主标题：SoulSoul（可替换为图片）
                    if (logoResId != null) {
                        Image(
                            painter = painterResource(id = logoResId),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(120.dp), // 根据实际素材调整大小
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            text = "SoulSoul",
                            fontSize = 72.sp,               // 更大更醒目
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFF5500),
                            letterSpacing = 2.sp             // 可选：让字距更开阔
                        )
                    }
                }
            }
        }

}