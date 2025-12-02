package com.bytedance.myapplication.ui

// ui/components/LoginScreen.kt
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults // 通常用于设置 TopAppBar 的颜色等
import androidx.compose.foundation.border
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text       // 加个 3！！！
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytedance.myapplication.ui.theme.OrangePrimary
import com.bytedance.myapplication.ui.theme.TextLight
// 确保你的 R 类可以被访问到，通常它会自动导入，但如果是不同的包，需要手动导入
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit

) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background // 使用背景色
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Log in",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 使用条款
            Text(
                text = "By logging in, you agree to our Terms of Use.",
                fontSize = 14.sp,
                color = TextLight,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Email 登录部分
            EmailLoginForm(onLoginSuccess)

            Spacer(modifier = Modifier.height(32.dp))

            // 分隔符 "Or"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text(" Or ", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(32.dp))

//            // 社交登录按钮
//            SocialLoginButton(
//                text = "Sign in with Google",
//                icon = R.drawable.ic_google, // 假设您有 Google 图标
//                onClick = { /* Handle Google login */ }
//            )
            /*Spacer(modifier = Modifier.height(16.dp))
            SocialLoginButton(
                text = "Sign in with Facebook",
                icon = R.drawable.ic_facebook, // 假设您有 Facebook 图标
                onClick = { *//* Handle Facebook login *//* }
            )
*/
            Spacer(modifier = Modifier.weight(1f)) // 将内容推到底部

//            // 隐私政策
//            Text(
//                text = "For more information, please see our Privacy policy.",
//                fontSize = 12.sp,
//                color = TextLight,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 8.dp)
//            )
        }
    }
}

@Composable
private fun EmailLoginForm(onLoginSuccess: () -> Unit) {
    val emailState = remember { mutableStateOf("") }

    Text(
        text = "Email",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    // 邮件输入框,创建一个在 Compose 里“会自动保存、内容变了会自动刷新 UI”的文本变量
    var email by remember { mutableStateOf("") }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Your email") },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrangePrimary,
            unfocusedBorderColor = Color(0xFFDDDDDD),
            focusedLabelColor = OrangePrimary,
            cursorColor = OrangePrimary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
//            containerColor = Color.White   // 最新版可以直接写 containerColor

        )
    )

    Text(
        text = "We will send you an e-mail with a login link.",
        fontSize = 12.sp,
        color = TextLight,
        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
    )

    // Connect 按钮
    Button(
        onClick = { onLoginSuccess() },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "Connect",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
private fun SocialLoginButton(text: String, icon: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 模拟图标
            // Image(
            //     painter = painterResource(id = icon),
            //     contentDescription = null,
            //     modifier = Modifier.size(24.dp).padding(end = 8.dp)
            // )
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}