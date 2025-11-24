# 崩溃排查指南

## 立即查看 Logcat

### 1. 打开 Logcat
- 在 Android Studio 底部点击 **Logcat** 标签
- 或者菜单：**View → Tool Windows → Logcat**

### 2. 过滤错误信息
在 Logcat 搜索框中输入以下关键词之一：
- `FATAL EXCEPTION` - 致命异常
- `AndroidRuntime` - Android运行时错误
- `Exception` - 所有异常
- `Error` - 所有错误
- `ChatRepository` - 查看API相关日志
- `ChatViewModel` - 查看ViewModel相关日志

### 3. 查看完整的堆栈信息
找到红色的错误日志，展开查看完整的堆栈跟踪（Stack Trace）

---

## 常见崩溃原因及解决方案

### 1. 网络权限问题
**错误信息**：`SecurityException: Permission denied`
**解决**：检查 `AndroidManifest.xml` 是否已添加网络权限

### 2. 空指针异常（NullPointerException）
**错误信息**：`NullPointerException`
**可能位置**：
- `ChatRepository.kt` 中解析JSON时
- `ChatViewModel.kt` 中访问消息列表时

### 3. JSON解析失败
**错误信息**：`JsonSyntaxException` 或 `IllegalStateException`
**可能原因**：API返回的JSON格式与 `ChatStreamChunk` 不匹配

### 4. 网络连接失败
**错误信息**：`UnknownHostException` 或 `SocketTimeoutException`
**解决**：检查网络连接和API URL

### 5. 线程问题
**错误信息**：`NetworkOnMainThreadException`
**解决**：确保网络请求在IO线程执行（已使用 `flowOn(Dispatchers.IO)`）

---

## 快速修复：增强异常处理

如果Logcat显示具体错误，请根据错误信息修复。以下是通用的增强异常处理：

