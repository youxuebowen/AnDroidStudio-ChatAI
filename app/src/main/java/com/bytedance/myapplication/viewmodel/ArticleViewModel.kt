package com.bytedance.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytedance.myapplication.Network.Article
import com.bytedance.myapplication.Network.ArticleRepository
import com.bytedance.myapplication.MVI.ProjectState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



/**
 * 文章数据的ViewModel，管理文章列表数据和加载状态
 */
class ArticleViewModel : ViewModel() {
    private val TAG = "ArticleViewModel"
    private val repository = ArticleRepository()

    // 文章列表数据的LiveData


    // 错误信息的LiveData

    // 项目状态的LiveData，整合所有状态
    private val _projectState = MutableStateFlow(ProjectState(currentArticle = null))
    val projectState: StateFlow<ProjectState> = _projectState.asStateFlow()


    /**
     * 加载文章列表数据，不用Intent了
     */
    fun loadArticles() {
        // 设置加载状态为true
        _projectState.update { it.copy(isLoading = true) }
        _projectState.update { it.copy(errorMessage = "") }
        viewModelScope.launch {
            try {
                // 调用repository获取文章列表,需要看一下response结构
                val response = repository.getArticleList()

                if (response != null) {
                    _projectState.update { it.copy(projectList = response.data) }
                    Log.d(TAG, "文章列表加载成功，共${response.data.size}篇文章")
                } else {
                    _projectState.update { it.copy(errorMessage = "500") }
                    Log.e(TAG, "文章列表加载失败: response为null")
                }
            } catch (e: Exception) {
                _projectState.update { it.copy(errorMessage = "500") }
                Log.e(TAG, "文章列表加载异常", e)
            } finally {
                // 无论成功失败，都设置加载状态为false并更新项目状态
                _projectState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * 处理文章选中状态变化
     */
    fun onArticleChecked(article: Article, isChecked: Boolean) {
        if (isChecked) {
            _projectState.update { it.copy(selectedArticleId = article.name as String) }
            _projectState.update { it.copy(currentArticle = article) }
            _projectState.update { it.copy(showDialog = true) }
        } else {
            _projectState.update { it.copy(selectedArticleId = "") }
            _projectState.update { it.copy(currentArticle = null) }
            _projectState.update { it.copy(showDialog = false) }
        }
    }

    /**
     * 关闭对话框并重置状态
     */
    fun dismissDialog() {
        _projectState.update { it.copy(selectedArticleId = "") }
        _projectState.update { it.copy(currentArticle = null) }
        _projectState.update { it.copy(showDialog = false) }
    }

    /**
     * 确认选中操作
     * @return 当前选中文章的URL，如果没有选中则返回null
     */
    fun confirmSelection(): String? {
        val description = (_projectState.value.currentArticle?.description ?: String())
        _projectState.update { it.copy(selectedArticleId = "") }
        _projectState.update { it.copy(currentArticle = null) }
        _projectState.update { it.copy(showDialog = false) }

            // 重置状态
        return description
    }
}
