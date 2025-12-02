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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bytedance.myapplication.R
import com.bytedance.myapplication.ui.theme.OrangePrimary
import com.bytedance.myapplication.ui.theme.PurpleGrey80

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = viewModel(),
    onFinished: () -> Unit,
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
                .background(OrangePrimary)
                .alpha(alpha)
                .scale(scale),
        ) {
            // 图片位于屏幕正中央
            Image(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(180.dp) // 您可以根据需要调整大小
                    .align(Alignment.Center),
                contentScale = ContentScale.Fit
            )

            // 文字位于屏幕下方
            Text(
                text = "SoulSoul",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PurpleGrey80,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp) // 与底部的距离
            )
        }
    }

}