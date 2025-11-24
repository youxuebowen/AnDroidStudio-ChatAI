package com.bytedance.myapplication.Network

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming
import retrofit2.http.Headers

/*使用 Retrofit 库定义的 Kotlin 接口（Interface），用于执行一个**流式（Streaming）**的网络请求。*/
interface ChatApiService {
    /*函数签名。suspend 关键字表示这是一个挂起函数，只能在协程或其他挂起函数中调用，确保网络请求不会阻塞主线程。
    *  @POST("") ，HTTP 方法和路径：指定该函数将使用 POST 方法发送请求。
    * @Body request: ChatApiRequest
    * 请求体：指定 request 参数（类型为 ChatApiRequest）的内容应该被序列化（通常是 JSON 格式）
    * 并作为 HTTP 请求的 Body 发送出去。
    * */
//    @Headers("Authorization: Bearer fefeb982-8d1a-428a-805f-f0e4318546f6",
//        "Content-Type: application/json")
    @POST("chat/completions") // 根据你的实际API端点修改
    @Streaming
    suspend fun streamChat(@Body request: ChatApiRequest): ResponseBody
    /*原始响应体：函数返回类型是 OkHttp 的 ResponseBody。当使用 @Streaming 时，这意味着 Retrofit
    不会自动为您处理响应体，而是将原始的字节流交给您（例如在 Repository 中）
    进行手动读取和解析（如您之前看到的 SSE 流解析代码）。*/
}



