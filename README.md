# AI聊天应用 (Android)

## 项目概述

这是一个基于Android平台开发的现代化AI聊天应用，使用Kotlin语言和Jetpack Compose框架构建。应用采用MVVM + MVI的架构模式，提供了与AI进行自然语言对话的功能，并支持会话管理和消息历史记录。

## 技术栈

- **开发语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构模式**: MVVM (Model-View-ViewModel) + MVI (Model-View-Intent)
- **网络请求**: Retrofit
- **本地存储**: Room Database
- **协程**: Kotlin Coroutines
- **状态管理**: Jetpack Compose State + MVI模式

## 项目结构

```
com.bytedance.myapplication/
├── MVI/                    # MVI架构相关文件
│   ├── ChatEffect.kt       # 聊天效果定义
│   ├── ChatIntent.kt       # 聊天意图定义
│   ├── ChatState.kt        # 聊天状态定义
│   ├── ErrorCode.kt        # 错误码定义
│   ├── ProjectState.kt     # 项目状态定义
│   ├── Screen.kt           # 屏幕定义
│   ├── SplashIntent.kt     # 启动意图定义
│   └── SplashState.kt      # 启动状态定义
├── MainActivity.kt         # 应用主入口
├── Network/                # 网络通信模块
│   ├── ApiClient.kt        # API客户端配置
│   ├── ArticleApiService.kt# 文章API服务
│   ├── ArticleListResponse.kt # 文章列表响应
│   ├── ArticleRepository.kt# 文章仓库
│   ├── ChatApiRequest.kt   # 聊天API请求
│   ├── ChatApiResponse.kt  # 聊天API响应
│   └── ChatApiService.kt   # 聊天API服务
├── Repository/             # 数据仓库
│   ├── ChatRepositoryAI.kt # AI聊天数据仓库
│   └── ChatRepositoryHistory.kt # 聊天历史仓库
├── data/                   # 数据模型
│   ├── ChatMessage.kt      # 聊天消息模型
│   ├── ChatSession.kt      # 聊天会话模型
│   └── database/           # 数据库相关
│       ├── AppDatabase.kt  # 应用数据库
│       ├── ChatDao.kt      # 聊天数据访问对象
│       ├── ChatMessageEntity.kt # 聊天消息实体
│       ├── ChatSessionEntity.kt # 聊天会话实体
│       └── UserEntity.kt   # 用户实体
├── ui/                     # UI界面
│   ├── ChatScreen.kt       # 聊天主界面
│   ├── LoginScreen.kt      # 登录界面
│   ├── ProjectScreen.kt    # 项目界面
│   ├── SplashScreen.kt     # 启动界面
│   ├── components/         # UI组件
│   │   ├── ChatInputBar.kt # 聊天输入栏
│   │   ├── ChatMessageBubble.kt # 聊天消息气泡
│   │   ├── ChatMessageList.kt # 聊天消息列表
│   │   ├── ChatTopBar.kt   # 聊天顶部栏
│   │   ├── DrawerContent.kt # 抽屉内容
│   │   ├── OnboardingScreen.kt # 引导界面
│   │   ├── ProjectCard.kt  # 项目卡片
│   │   ├── SessionItem.kt  # 会话项
│   │   └── WalkWinAPP.kt   # 步行窗口应用组件
│   └── theme/              # 主题配置
│       ├── Color.kt        # 颜色定义
│       ├── Theme.kt        # 主题定义
│       └── Type.kt         # 字体类型定义
└── viewmodel/              # ViewModel层
    ├── ArticleViewModel.kt # 文章ViewModel
    ├── ChatViewModel.kt    # 聊天ViewModel
    └── SplashViewModel.kt  # 启动ViewModel
```

## 核心功能

1. **AI聊天功能**
   - 与AI进行自然语言对话
   - 支持发送文本消息
   - 实时显示AI回复

2. **会话管理**
   - 创建新的聊天会话
   - 切换不同的聊天会话
   - 删除聊天会话

3. **消息历史**
   - 保存聊天历史记录
   - 加载历史消息
   - 支持会话内消息查询

4. **用户界面**
   - 现代化的聊天界面设计
   - 支持深色/浅色主题
   - 响应式布局，适配不同屏幕尺寸
   - 流畅的动画效果

## 架构设计

### MVI架构

应用采用MVI架构模式，主要包含以下组件：

- **Intent**: 用户操作的意图，如发送消息、切换会话等
- **State**: 应用的状态，包括消息列表、会话列表、加载状态等
- **Effect**: 一次性的副作用，如显示Toast、导航等

### 数据流向

1. 用户操作产生Intent
2. ViewModel处理Intent，更新State
3. Compose UI观察State变化并重新渲染
4. 副作用通过Effect处理

## 开发环境要求

- Android Studio Arctic Fox或更高版本
- Android SDK 31或更高版本
- Kotlin 1.6或更高版本
- Gradle 7.0或更高版本

## 构建和运行

1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 等待Gradle同步完成
4. 连接Android设备或启动模拟器
5. 点击Run按钮运行应用

## 项目亮点

1. **现代化UI**: 使用Jetpack Compose构建的响应式UI，提供流畅的用户体验
2. **清晰的架构**: 采用MVVM + MVI架构模式，使代码结构清晰，易于维护和扩展
3. **良好的可测试性**: 各层之间低耦合，便于单元测试和集成测试
4. **高效的状态管理**: 使用MVI模式管理应用状态，使状态变化可预测
5. **优化的性能**: 使用Kotlin协程处理异步操作，确保应用响应迅速

## 许可证

本项目采用MIT许可证，详情请参阅LICENSE文件。
