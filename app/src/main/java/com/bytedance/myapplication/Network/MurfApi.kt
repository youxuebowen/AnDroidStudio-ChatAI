package com.bytedance.myapplication.Network
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming

// 1. 定义请求体数据结构 (对照 Python 的 data 字典)
data class TtsRequest(
    val voice_id: String = "en-US-matthew",
    val text: String,
    val multi_native_locale: String = "en-US",
    val model: String = "FALCON",
    val format: String = "MP3",
    val sampleRate: Int = 24000,
    val channelType: String = "MONO",
    val rate: Int = -50
)

// 2. 定义 Retrofit 接口
interface MurfApiService {
    // 对应 URL: https://global.api.murf.ai/v1/speech/stream
    @POST("v1/speech/stream")
    @Streaming // 重要：因为返回的是音频流二进制数据，需要标记为 Streaming
    suspend fun generateSpeech(
        @Header("api-key") apiKey: String, // 通过 Header 传入 Key
        @Header("Content-Type") contentType: String = "application/json",
        @Body requestBody: TtsRequest
    ): Response<ResponseBody>
}

// 3. 单例 Network Client
object RetrofitClient {
    private const val BASE_URL = "https://global.api.murf.ai/"

    // 警告：实际开发中请勿硬编码 API Key
    const val API_KEY = "ap2_e2211329-fbbe-436a-a635-f757181f04b0"

    val murfApi: MurfApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MurfApiService::class.java)
    }
}