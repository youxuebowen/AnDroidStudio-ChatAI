package com.bytedance.myapplication.Network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 文章数据仓库，负责处理与文章相关的网络请求
 */
class ArticleRepository {
    private val TAG = "ArticleRepository"
    private val apiService = ApiClient.articleApiService
    
    /**
     * 获取文章列表数据
     * @return ArticleListResponse 文章列表响应对象，如果请求失败返回null
     */
    suspend fun getArticleList(): ArticleListResponse? {
        return withContext(Dispatchers.IO) {
            try {
                // 调用API服务获取文章列表
                val response = apiService.getArticleList()
                Log.d(TAG, "获取文章列表成功，共${response.data.size}篇文章")
                response
            } catch (e: Exception) {
                // 捕获并记录请求异常
                Log.e(TAG, "获取文章列表失败: ${e.message}", e)
                null
            }
        }
    }
}