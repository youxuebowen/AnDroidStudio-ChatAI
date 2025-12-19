package com.bytedance.myapplication.Network

import com.bytedance.myapplication.data.BaiduResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

// 1. 定义接口
interface BaiduApiService {
    @FormUrlEncoded
    @POST("rest/2.0/image-classify/v2/advanced_general")
    suspend fun classifyImage(
        @Header("Authorization") auth: String, // 传入时需包含 "Bearer ..."
        @Field("image") imageBase64: String    // 对应百度要求的 image 字段
    ): BaiduResponse
}

// 2. 单例客户端
object RetrofitClientBaidu {
    private const val BASE_URL = "https://aip.baidubce.com/"

    val baiduApi: BaiduApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BaiduApiService::class.java)
    }
}