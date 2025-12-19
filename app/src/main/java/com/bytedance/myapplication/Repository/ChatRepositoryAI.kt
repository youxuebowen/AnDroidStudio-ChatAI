package com.bytedance.myapplication.Repository

import com.bytedance.myapplication.data.ChatMessage
import com.bytedance.myapplication.data.MessageRole
import com.bytedance.myapplication.Network.ApiClient
import com.bytedance.myapplication.Network.ChatApiRequest
import com.bytedance.myapplication.Network.ChatStreamChunk
import com.bytedance.myapplication.Network.ImageGenerationApiRequest
import com.bytedance.myapplication.Network.ImageGenerationApiResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers // FIX: 用于 flowOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn // FIX
import kotlinx.coroutines.withContext
import android.util.Log
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.BufferedReader
import java.io.InputStreamReader


/*OpenAI 官方 API
豆包（字节火山引擎）
讯飞、智谱 SSE 流式接口
任何兼容 OpenAI format 的第三方模型*/
class ChatRepositoryAI {

    private val apiService = ApiClient.chatApiService
    private val gson = Gson()

    private val systemPrompt = "你是一个有用的AI助手。"
    private val defaultImageModel = "doubao-seedream-4-5-251128"

    /*这个函数会随时间流式地产生（发射）字符串（即 AI 回复的每个 token）*/
    fun streamChat(
        messages: List<ChatMessage>,
        model: String = "doubao-seed-1-6-flash-250828",
//        apikey: String = "Bearer fefeb982-8d1a-428a-805f-f0e4318546f6"
    ): Flow<String> = flow {
        /* = flow这是 Flow 构建器。它将 {} 中的代码转换为一个冷流（Cold Flow）的生产者。当有人开始 collect 时，这块代码才会被执行。*/
        // ---------------- FIX 1：所有 IO 代码放 try 包裹，确保资源释放 ----------------
        try {
            /*用于初始化一个可变的列表，专门用来存储 API 请求中所需的消息数据。
            * 列表的元素 是 Map 对象，而每个 Map 的键（Key）和值（Value）都必须是 String 类型。*/
            val apiMessages = mutableListOf<Map<String, String>>()
            /*“遍历 messages 列表，如果列表中存在任何一条消息的 role 是 SYSTEM（系统消息），
            那么 hasSystemMessage 变量的值就为 true；否则为 false。”*/
            val hasSystemMessage = messages.any { it.role == MessageRole.System }

            /*检查并确保消息历史中存在一条系统级别的指令（System Prompt）。*/
            if (!hasSystemMessage) {
                apiMessages.add(mapOf("role" to "system", "content" to systemPrompt))
            }

            /*遍历消息列表，
            它的作用是将本地的消息对象 msg 转换为 API 接口所要求的格式*/
            messages.forEach { msg ->
                apiMessages.add(msg.toApiMessage())
            }

            val request = ChatApiRequest(
                model = model,
                messages = apiMessages,
                stream = true,
//                Authorization = apikey
            )

            // ------------ FIX 2：streaming 的接口必须是 @Streaming，否则会 OOM ------------
            // 添加详细的错误处理
            val response: ResponseBody = try {
                /*执行实际的网络请求。*/
                apiService.streamChat(request)
            } catch (e: HttpException) {
                // HTTP错误（401, 403, 404, 500等）
                val errorBody = e.response()?.errorBody()?.string() ?: "无错误详情"
                Log.e("ChatRepository", "HTTP错误: ${e.code()} - ${e.message()}")
                Log.e("ChatRepository", "错误响应体: $errorBody")
                throw ChatApiException("HTTP错误 ${e.code()}: ${e.message()}\n错误详情: $errorBody", e)
            } catch (e: java.net.SocketTimeoutException) {
                Log.e("ChatRepository", "请求超时", e)
                throw ChatApiException("请求超时，请检查网络连接", e)
            } catch (e: java.net.UnknownHostException) {
                Log.e("ChatRepository", "无法解析主机", e)
                throw ChatApiException("无法连接到服务器，请检查网络和URL配置", e)
            } catch (e: java.io.IOException) {
                Log.e("ChatRepository", "IO异常", e)
                throw ChatApiException("网络IO异常: ${e.message}", e)
            } catch (e: Exception) {
                Log.e("ChatRepository", "未知异常", e)
                Log.e("ChatRepository", "异常类型: ${e.javaClass.name}")
                Log.e("ChatRepository", "异常消息: ${e.message}")
                e.printStackTrace()
                throw ChatApiException("API调用失败: ${e.message}", e)
            }
            
            // 🔴 断点1：在这里设置断点，可以查看 response 对象
            // 在Debugger中查看：response.contentType(), response.contentLength()
            Log.d("ChatRepository", "=== API响应成功 ===")
            Log.d("ChatRepository", "Response ContentType: ${response.contentType()}")
            Log.d("ChatRepository", "Response ContentLength: ${response.contentLength()}")
            
            // 检查响应状态
            if (response.contentLength() == 0L) {
                Log.w("ChatRepository", "警告: 响应体为空")
            }

            // ------------ FIX 3：用 UTF-8 StandardCharsets.UTF_8 更安全 ------------
            val reader = BufferedReader(
                InputStreamReader(response.byteStream(), Charsets.UTF_8)
            )

            try {
                var line: String?

                while (reader.readLine().also { line = it } != null) {

                    val raw = line?.trim() ?: continue
                    if (raw.isEmpty()) continue  // FIX: 跳过空行

                    // 断点2：在这里设置断点，查看原始SSE行数据
                    // 在Debugger中查看：raw 变量的值，可以看到完整的SSE格式数据
                    Log.d("ChatRepository", "SSE原始行: $raw")

                    if (!raw.startsWith("data: ")) continue  // FIX: 安全过滤

                    val jsonData = raw.substring(6).trim()

                    // 🔴 断点3：在这里设置断点，查看提取的JSON数据
                    // 在Debugger中查看：jsonData 变量的值，这是纯JSON字符串
                    Log.d("ChatRepository", "JSON数据: $jsonData")

                    if (jsonData == "[DONE]") {
                        Log.d("ChatRepository", "收到 [DONE] 信号，流结束")
                        break
                    }

                    // ------------- FIX 4：try-catch 防止单行解析失败导致中断 -------------
                    try {
                        val chunk = gson.fromJson(jsonData, ChatStreamChunk::class.java)
                        
                        // 🔴 断点4：在这里设置断点，查看解析后的chunk对象
                        // 在Debugger中可以展开查看：
                        //   - chunk.choices
                        //   - chunk.choices[0].delta
                        //   - chunk.choices[0].delta.content
                        Log.d("ChatRepository", "解析后的Chunk: id=${chunk.id}, choices=${chunk.choices?.size}")

                        // FIX 5：delta 可能为 {} ，必须判空
                        val content = chunk.choices
                            ?.firstOrNull()
                            ?.delta
                            ?.content

                        if (content != null) {
                            // 🔴 断点5：在这里设置断点，查看提取的content token
                            // 在Debugger中查看：content 变量的值，这是单个token
                            Log.d("ChatRepository", "提取的Token: '$content'")
                            emit(content)
                        } else {
                            Log.d("ChatRepository", "Chunk中没有content，跳过")
                        }

                    } catch (e: Exception) {
                        // JSON可能不完整或包含log，跳过但不中断流
                        e.printStackTrace()
                    }
                }

            } finally {
                // --------- FIX 6：始终关闭流，避免 socket 泄漏 ----------
                reader.close()
                response.close()
            }

        } catch (e: Exception) {
            // 记录详细的错误信息
            Log.e("ChatRepository", "API调用失败", e)
            Log.e("ChatRepository", "错误类型: ${e.javaClass.simpleName}")
            Log.e("ChatRepository", "错误消息: ${e.message}")
            e.printStackTrace()
            throw ChatApiException("API调用失败: ${e.message}", e)
        }

        // -------- FIX 7：流式网络必须在 IO 线程运行 --------
    }.flowOn(Dispatchers.IO)
    
    /**
     * 生成图像
     * @param prompt 图像生成提示词
     * @param model 使用的模型，默认使用豆包的图像模型
     * @return 图像生成响应
     */
    suspend fun generateImage(
        prompt: String,
        model: String = defaultImageModel
    ): ImageGenerationApiResponse = withContext(Dispatchers.IO) {
        try {
            val request = ImageGenerationApiRequest(
                model = model,
                prompt = prompt,
                size = "2K",
                watermark = false
            )
            
            val response = apiService.generateImage(request)
            Log.d("ChatRepositoryAI", "图像生成成功: ${response.data.size}张图像")
            response
        } catch (e: HttpException) {
            // HTTP错误处理
            val errorBody = e.response()?.errorBody()?.string() ?: "无错误详情"
            Log.e("ChatRepositoryAI", "图像生成HTTP错误: ${e.code()} - ${e.message()}")
            Log.e("ChatRepositoryAI", "错误响应体: $errorBody")
            throw ChatApiException("图像生成HTTP错误 ${e.code()}: ${e.message()}\n错误详情: $errorBody", e)
        } catch (e: Exception) {
            Log.e("ChatRepositoryAI", "图像生成失败: ${e.message}", e)
            throw ChatApiException("图像生成失败: ${e.message}", e)
        }
    }
}
// 自定义异常类
class ChatApiException(message: String, cause: Throwable? = null) : Exception(message, cause)

