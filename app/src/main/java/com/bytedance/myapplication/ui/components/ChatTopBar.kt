package com.bytedance.myapplication.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import com.bytedance.myapplication.R
import com.bytedance.myapplication.ui.theme.*



//@OptIn(...) 就是告诉编译器：“我知道这个 API 是实验性的，自愿使用它”，不加这个注解会报错；
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(onMenuClick: () -> Unit, onInfoClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        shadowElevation = 8.dp
    ) {
        TopAppBar(
            title = { Text("SoulSoul", color = MaterialTheme.colorScheme.onPrimary) },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "菜单", tint = MaterialTheme.colorScheme.onPrimary)
                }
            },
            actions = {
                IconButton(onClick = onInfoClick) {
                    Icon(painter = painterResource(id = R.drawable.ic_project), contentDescription = "详情", tint = MaterialTheme.colorScheme.onPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer,
                containerColor = OrangePrimary
//            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}