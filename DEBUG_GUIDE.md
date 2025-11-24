# 调试指南：如何查看远程API返回的内容

## 方法一：使用断点调试（推荐）

### 关键断点位置

#### 1. **查看API响应对象** 
**文件**: `ChatRepository.kt` 第54行
```kotlin
val response: ResponseBody = apiService.streamChat(request)
```
- 在这里可以查看 `response` 对象
- 在Debugger中展开查看：
  - `response.contentType()` - 响应类型
  - `response.contentLength()` - 响应长度
  - `response.byteStream()` - 原始字节流

#### 2. **查看原始SSE行数据**
**文件**: `ChatRepository.kt` 第66行
```kotlin
val raw = line?.trim() ?: continue
```
- 在这里可以查看每一行SSE原始数据
- 格式通常是：`data: {"id":"...","choices":[...]}`
- 在Debugger中查看 `raw` 变量的值

#### 3. **查看提取的JSON数据**
**文件**: `ChatRepository.kt` 第71行
```kotlin
val jsonData = raw.substring(6).trim()
```
- 在这里可以查看去掉 `data: ` 前缀后的纯JSON字符串
- 在Debugger中查看 `jsonData` 变量的值

#### 4. **查看解析后的Chunk对象**
**文件**: `ChatRepository.kt` 第77行
```kotlin
val chunk = gson.fromJson(jsonData, ChatStreamChunk::class.java)
```
- 在这里可以查看解析后的完整对象结构
- 在Debugger中展开查看：
  - `chunk.id` - 消息ID
  - `chunk.choices` - 选择列表
  - `chunk.choices[0].delta` - 增量数据
  - `chunk.choices[0].delta.content` - 实际内容

#### 5. **查看提取的Token**
**文件**: `ChatRepository.kt` 第85行
```kotlin
if (content != null) {
    emit(content)
}
```
- 在这里可以查看每个提取出的token
- 在Debugger中查看 `content` 变量的值

#### 6. **查看ViewModel接收的Token**
**文件**: `ChatViewModel.kt` 第155行
```kotlin
).collect { token ->
```
- 在这里可以查看ViewModel层接收到的每个token
- 在Debugger中查看：
  - `token` - 单个token
  - `accumulatedContent` - 累积的完整内容

### 如何设置断点

1. 在Android Studio中，点击代码行号左侧的空白处
2. 出现红色圆点表示断点已设置
3. 运行Debug模式（点击🐛图标或按 Shift+F9）
4. 当代码执行到断点时会暂停
5. 在Debugger窗口可以查看所有变量值

### 断点调试技巧

- **条件断点**：右键断点 → 设置条件（如 `content != null`）
- **日志断点**：右键断点 → 取消勾选"Suspend" → 添加日志表达式
- **临时禁用**：右键断点 → 取消勾选断点

---

## 方法二：使用Logcat查看日志

### 查看位置

在Android Studio的 **Logcat** 窗口中，过滤标签：
- `ChatRepository` - 查看API请求和响应详情
- `ChatViewModel` - 查看ViewModel层的处理

### 日志输出内容

1. **API请求信息**
   ```
   D/ChatRepository: === API请求 ===
   D/ChatRepository: Model: gpt-3.5-turbo
   D/ChatRepository: Messages: [{role=system, content=...}, ...]
   ```

2. **API响应信息**
   ```
   D/ChatRepository: === API响应 ===
   D/ChatRepository: Response ContentType: text/event-stream
   ```

3. **SSE原始行**
   ```
   D/ChatRepository: SSE原始行: data: {"id":"chatcmpl-xxx","choices":[...]}
   ```

4. **JSON数据**
   ```
   D/ChatRepository: JSON数据: {"id":"chatcmpl-xxx","choices":[...]}
   ```

5. **解析后的Chunk**
   ```
   D/ChatRepository: 解析后的Chunk: id=chatcmpl-xxx, choices=1
   ```

6. **提取的Token**
   ```
   D/ChatRepository: 提取的Token: '你'
   D/ChatRepository: 提取的Token: '好'
   ```

7. **ViewModel接收的Token**
   ```
   D/ChatViewModel: 收到Token: '你', 累积内容长度: 1
   ```

---

## 方法三：使用HttpLoggingInterceptor（已在代码中启用）

### 查看位置

在Logcat中过滤：`OkHttp`

### 查看内容

- **请求头**：包括Authorization、Content-Type等
- **请求体**：完整的JSON请求
- **响应头**：包括状态码、Content-Type等
- **响应体**：SSE流式数据（可能很长）

---

## 方法四：保存完整响应到文件（用于深度分析）

如果需要保存完整的API响应进行分析，可以在 `ChatRepository.kt` 中添加：

```kotlin
// 在读取响应前添加
val responseBytes = response.bytes()
val responseString = String(responseBytes, Charsets.UTF_8)
Log.d("ChatRepository", "完整响应: $responseString")

// 或者保存到文件
val file = File(context.getExternalFilesDir(null), "api_response.txt")
file.writeText(responseString)
```

---

## 常见问题排查

### 1. 看不到响应内容？
- 检查网络权限是否已添加
- 检查API URL和认证是否正确
- 查看Logcat中的异常信息

### 2. JSON解析失败？
- 在断点4查看 `jsonData` 的原始内容
- 检查JSON格式是否符合 `ChatStreamChunk` 结构
- 可能需要调整 `ChatStreamChunk` 数据类

### 3. 流式数据中断？
- 在断点2查看是否有 `[DONE]` 信号
- 检查网络连接是否稳定
- 查看是否有异常被捕获

---

## 快速调试步骤

1. **设置断点**：在 `ChatRepository.kt` 第71行（查看JSON数据）
2. **运行Debug**：点击🐛图标
3. **发送消息**：在应用中发送一条测试消息
4. **查看变量**：在Debugger中查看 `jsonData` 和 `chunk` 对象
5. **查看日志**：在Logcat中查看 `ChatRepository` 标签的日志

