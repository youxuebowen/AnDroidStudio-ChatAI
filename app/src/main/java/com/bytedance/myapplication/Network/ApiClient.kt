package com.bytedance.myapplication.Network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import android.util.Log
object ApiClient {
    // 用于存储豆包的连接和API
    // 注意：Retrofit要求baseUrl必须以斜杠结尾
    /*Retrofit 就会在运行时自动生成执行 HTTP 请求的代码。
    * 它负责将 Kotlin/Java 对象序列化成 JSON（用于请求体），
    * 并将服务器返回的 JSON 反序列化回 Kotlin/Java 对象（如 data class）*/
    private const val BASE_URL = "https://ark.cn-beijing.volces.com/api/v3/"

//    日志
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // ⚠️ 警告：以下代码仅用于开发环境，信任所有SSL证书
    // 生产环境应该使用正确的证书验证
    /*如果说 Retrofit 是一个“翻译官”，将接口转换为请求，
    那么 OkHttpClient 就是实际的“执行者”，负责建立网络连接、发送原始字节数据、接收响应并处理底层协议。
    OkHttpClient 使用 Builder 模式进行配置，允许您在创建客户端实例时，灵活地设置超时时间、拦截器、证书、代理等各种参数。*/
    private fun createUnsafeOkHttpClient(): OkHttpClient {
        /*“创建一个特殊的 X509TrustManager，它重写了所有证书验证方法，
        但方法体为空，从而阻止了任何实际的证书检查。
        这个管理器随后被添加到 OkHttpClient 的配置中。”*/
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory
        
        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true } // 信任所有主机名
            .addInterceptor(loggingInterceptor)
            // 修正后的拦截器逻辑：只执行一次请求，并返回结果
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer fefeb982-8d1a-428a-805f-f0e4318546f6")
                    .header("Content-Type", "application/json")
                    .build()

                val response = chain.proceed(request)

                // ⚠️ 注意：不要在这里读取 response.body().string()，否则流会被消耗！
                // 仅读取 response.code/headers 是安全的。
                if (response.isSuccessful) {
                    Log.d("HTTP", "请求成功，状态码 = ${response.code}")
                } else {
                    Log.e("HTTP", "请求失败，状态码 = ${response.code}")
                }

                return@addInterceptor response // 必须将响应返回给下一个拦截器或 Retrofit
            }
//            .addInterceptor { chain ->
//                val original = chain.request() //获取拦截到的原始请求对象。
//                val requestBuilder = original.newBuilder()
//                    // 火山引擎API认证：根据实际API文档调整格式
//                    // 如果是Bearer token格式，使用: .header("Authorization", "Bearer fefeb982-8d1a-428a-805f-f0e4318546f6")
//                    // 如果是直接token，使用当前格式
//                    .header("Authorization", "Bearer fefeb982-8d1a-428a-805f-f0e4318546f6")
//                    .header("Content-Type", "application/json")
//                    .header("Accept", "text/event-stream")
////                    .build()
//                val response = chain.proceed(requestBuilder.build())
////                 val response = chain.proceed(requestBuilder)
//
//                if (response.isSuccessful) {
//                    Log.d("HTTP", "请求成功，状态码 = ${response.code}")
//                } else {
//                    Log.e("HTTP", "请求失败，状态码 = ${response.code}")
//                }
//
//
//                val request = requestBuilder.build()
//                chain.proceed(request)
//            }
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }
    
    // 使用不安全的客户端（仅用于开发环境解决SSL证书问题）
    private val okHttpClient = createUnsafeOkHttpClient()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val chatApiService: ChatApiService = retrofit.create(ChatApiService::class.java)
}



