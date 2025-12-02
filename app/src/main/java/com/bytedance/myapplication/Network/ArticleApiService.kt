package com.bytedance.myapplication.Network

import retrofit2.http.GET

/**
 * 文章列表API服务接口，用于从服务器获取文章列表数据
 */
interface ArticleApiService {
    /**
     * 获取文章列表的GET请求方法
     * @return ArticleListResponse 文章列表响应对象
     */
    @GET("http://8.130.154.167:18080/api/v2/get_favor")
    suspend fun getArticleList(): ArticleListResponse
}