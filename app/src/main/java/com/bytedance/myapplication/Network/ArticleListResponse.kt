package com.bytedance.myapplication.Network

/**
 * 文章列表响应数据类，用于解析从API返回的JSON数据
 */
data class ArticleListResponse(
    val success: Boolean,  // 请求是否成功
    val data: List<Article>,  // 文章列表数据
    val count: Int  // 文章总数
)
data class Article(
    val name: String?,  // 文章标题
    val url: String? ,// 文章URL链接
    val content: String?
)