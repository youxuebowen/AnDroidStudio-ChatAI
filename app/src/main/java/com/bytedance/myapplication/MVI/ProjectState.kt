package com.bytedance.myapplication.MVI

import android.R
import com.bytedance.myapplication.Network.Article

// 项目状态数据类
data class ProjectState(
    val projectList: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val selectedArticleId: String = "",
    val showDialog: Boolean = false,
    val currentArticle: Article?,
    val isChecked: Boolean =false
)
//    private val coroutineScope: CoroutineScope,
//) {
//    // 加载文章列表的方法
//    fun loadArticles() {
//        viewModel.loadArticles()
//    }
//}