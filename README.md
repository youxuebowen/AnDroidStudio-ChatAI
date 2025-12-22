# SoulSoul AI助手

## 项目概述

SoulSoul AI助手是一款基于Android平台开发的现代化AI助手，采用Kotlin语言与Jetpack Compose框架构建，依托MVVM + MVI架构模式打造稳定高效的产品体验。应用不仅提供AI自然语言对话、会话管理、消息历史记录、图像生成与显示等核心功能，更聚焦**日常交互**、**热点技术信息追踪**、**英语学习**三大核心需求；历经持续迭代优化，现已升级至2.0版本，未来将持续打磨升级，致力于成为服务**个人开发者、英语学习者**的实用型个人AI助手。

具体升级与功能亮点如下： 

- 交互体验升级：对产品交互界面进行针对性优化，例如聊天框、底部导航采用悬浮胶囊设计，进一步提升操作便捷性与视觉舒适度，让用户交互更流畅。
-  热点技术精准推送：新增每日更新的热点技术信息推送功能，无需用户主动检索，即可实时获取行业前沿、精准新鲜的技术资讯，助力个人开发者紧跟技术趋势。 
- 拍照学英语痛点解决方案：针对英语学习者“难以将日常生活所见事物转化为对应英语单词”的核心痛点，开发拍照学英语功能。该功能深度整合双大模型能力——大模型一负责精准识别图片中的物品，大模型二将识别结果转化为标准语音；配套复习界面还会集中展示图片、英文单词、中文释义及语音资源，形成“识别-发音-复习”的闭环学习链路，帮助用户高效记忆与巩固。

## 技术栈

- **开发语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构模式**: MVVM (Model-View-ViewModel) + MVI (Model-View-Intent)
- **网络请求**: Retrofit
- **本地存储**: Room Database
- **协程**: Kotlin Coroutines
- **状态管理**: Jetpack Compose State + MVI模式
- **图像加载**: Coil Compose

## 核心功能

1. **智能对话交互**

   作为核心基础功能，支持与 AI 进行自然语言交互，支持文本消息发送，AI 回复实时呈现且附带打字机逐字显示效果，还原真实对话体验。

2. **AI拍照学英语**

   针对英语学习中 “实景事物难转化为英文表达” 的痛点，打造 “拍照识别 - 语言转化 - 复习巩固” 全闭环学习体验：支持一键触发拍照功能，AI 自动识别画面中的物体，精准完成物体名称的中英互译，并生成对应英文标准语音；复习界面整合多维度学习资源，同步展示拍摄原图、英文发音音频、英文单词 / 短语及中文释义，多感官联动强化记忆效果，提升实景英语学习效率。

3. **热点技术信息追踪**

   聚焦技术前沿资讯获取需求，支持热点技术项目推送列表展示；优化项目卡片交互体验，点击后可将项目 URL 自动填充至聊天界面，便于快速基于热点内容与 AI 展开交流；针对加载环节进行专项优化，完善加载状态可视化反馈及错误处理机制，保障资讯获取的稳定性与流畅性。

4. **会话全生命周期管理**

   覆盖会话全流程操作：支持新建聊天会话、多会话自由切换及冗余会话删除；同时实现聊天历史记录的持久化保存、按需加载，以及会话内消息精准查询。

5. **沉浸式界面体验**

   采用现代化设计打造聊天界面，基于响应式布局适配不同屏幕尺寸，保障多设备使用一致性；融入流畅的动画效果，优化操作交互的视觉体验。

## 运行环境要求

| 环境/工具 | 版本要求 | 说明 |
|-----------|----------|------|
| Android Studio | Hedgehog (2023.1.1) 或更高 | 推荐使用最新稳定版 |
| Android SDK | API Level 34 (Android 14) | 编译SDK版本 |
| Android SDK | API Level 24 (Android 7.0) | 最低支持版本 |
| Kotlin | 1.9.0 或更高 | Kotlin编程语言 |
| Gradle | 8.0 或更高 | 构建工具 |
| Java JDK | 1.8 或更高 | Java开发工具包 |
| Android 模拟器 | API Level 24+ | 或使用真实Android设备 |

## 依赖库及安装命令

### 核心依赖库

| 依赖库 | 版本 | 用途 |
|--------|------|------|
| Room Database | 2.6.1 | 本地数据存储 |
| Retrofit | 2.9.0 | 网络请求框架 |
| OkHttp | 4.12.0 | HTTP客户端 |
| Jetpack Compose | 2024.02.00 | UI框架 |
| Kotlin Coroutines | 1.7.3 | 异步编程 |
| Coil Compose | 2.5.0 | 图像加载 |
| Gson | 2.10.1 | JSON解析 |
| Navigation Compose | 2.7.5 | 应用导航 |
| ML Kit Translate | 17.0.3 | 翻译功能 |

### 安装方法

1. 克隆项目到本地：
   ```bash
   git clone https://github.com/your-repo/soulsoul-ai-assistant.git
   cd soulsoul-ai-assistant
   ```

2. 使用Android Studio打开项目：
   - 启动Android Studio
   - 选择"Open an existing project"
   - 导航到项目目录并选择打开

3. 依赖安装：
   - Android Studio会自动检测并下载所有依赖库
   - 等待Gradle同步完成（首次同步可能需要较长时间）

4. 手动同步（如果自动同步失败）：
   - 点击Android Studio工具栏中的"Sync Project with Gradle Files"按钮
   - 或执行命令：
     ```bash
     ./gradlew clean build --refresh-dependencies
     ```

## 详细运行步骤

### 步骤1：配置开发环境

1. **安装Android Studio**：
   - 访问[Android Studio官方网站](https://developer.android.com/studio)
   - 下载并安装最新版本的Android Studio
   - 安装过程中选择默认选项即可

2. **配置Android SDK**：
   - 启动Android Studio
   - 打开SDK Manager（Tools > SDK Manager）
   - 确保安装以下组件：
     - Android SDK Platform 34
     - Android SDK Build-Tools 34.0.0
     - Android Emulator
     - Android SDK Platform-Tools
     - Intel x86 Emulator Accelerator (HAXM installer)（可选，用于加速模拟器）

3. **配置模拟器或连接设备**：
   - **使用模拟器**：
     - 打开AVD Manager（Tools > AVD Manager）
     - 点击"Create Virtual Device"
     - 选择一个设备配置（如Pixel 6）
     - 选择系统镜像（推荐API Level 34）
     - 完成模拟器创建
   - **使用真实设备**：
     - 在设备上启用开发者选项和USB调试
     - 使用USB线将设备连接到电脑
     - 按照屏幕提示授权USB调试

### 步骤2：打开并配置项目

1. **打开项目**：
   - 启动Android Studio
   - 选择"Open an existing project"
   - 导航到项目目录并选择打开

2. **等待Gradle同步**：
   - Android Studio会自动开始Gradle同步
   - 首次同步可能需要几分钟时间，因为需要下载所有依赖库
   - 确保同步成功（右下角会显示"Sync successful"）

3. **检查配置**：
   - 确认`app/build.gradle.kts`文件中的依赖配置正确
   - 确认编译SDK版本为34，最低SDK版本为24

### 步骤3：运行应用

1. **选择运行目标**：
   - 在Android Studio工具栏的"Device Manager"下拉菜单中，选择一个已配置的模拟器或已连接的真实设备

2. **启动应用**：
   - 点击工具栏中的"Run"按钮（绿色三角形图标）
   - 或按下快捷键`Shift + F10`
   - 或执行命令：
     ```bash
     ./gradlew installDebug
     ```

3. **等待编译和安装**：
   - Android Studio会编译项目并将APK安装到目标设备上
   - 首次编译可能需要较长时间
   - 应用安装完成后会自动启动

4. **验证应用运行**：
   - 确认应用成功启动并显示Splash屏幕
   - 导航到聊天界面，尝试发送消息
   - 确认AI回复正常显示

## 项目结构

```
com.bytedance.myapplication/
├── MVI/                    # MVI架构相关文件
├── MainActivity.kt         # 应用主入口
├── Network/                # 网络通信模块
├── Repository/             # 数据仓库
├── data/                   # 数据模型
├── ui/                     # UI界面
└── viewmodel/              # ViewModel层
```

## 打包APK

### 使用Android Studio GUI打包

1. 点击顶部菜单栏的 `Build` → `Generate Signed Bundle / APK...`
2. 选择 `APK` 并点击 `Next`
3. 配置签名密钥库（创建新的或选择现有）
4. 选择构建变体（Release 或 Debug）
5. 点击 `Finish` 按钮开始打包
6. APK文件将保存在 `app/build/outputs/apk/` 目录下

### 使用命令行打包

```bash
# 生成Debug APK
./gradlew assembleDebug

# 生成Release APK
./gradlew assembleRelease
```

打包完成后，APK文件将保存在以下位置：
- Debug APK：`app/build/outputs/apk/debug/万博闻-Chatbot.apk`
- Release APK：`app/build/outputs/apk/release/万博闻-Chatbot.apk`

## 项目亮点

1. **现代化UI**: 使用Jetpack Compose构建的响应式UI，提供流畅的用户体验
2. **清晰的架构**: 采用MVVM + MVI架构模式，使代码结构清晰，易于维护和扩展
3. **良好的可测试性**: 各层之间低耦合，便于单元测试和集成测试
4. **高效的状态管理**: 使用MVI模式管理应用状态，使状态变化可预测
5. **优化的性能**: 使用Kotlin协程处理异步操作，确保应用响应迅速
6. **图像识别与语音转换**: 集成图像识别API并支持自动识别和显示图像，将图像转换为中英文字与语音
7. **打字机效果**: AI回复采用逐字显示的打字机效果，提升用户体验

## 联系方式

如有任何问题或建议，请通过以下方式联系我们：
- 邮箱：[2215225145@qq.com]
- GitHub：[https://github.com/youxuebowen/AnDroidStudio-ChatAI.git]
