package com.bytedance.myapplication.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
//@OptIn(...) 就是告诉编译器：“我知道这个 API 是实验性的，自愿使用它”，不加这个注解会报错；
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = { Text("即梦AI") },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "菜单")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}