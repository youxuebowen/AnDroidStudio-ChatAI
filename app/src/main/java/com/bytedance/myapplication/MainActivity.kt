// com/bytedance/myapplication/MainActivity.kt

package com.bytedance.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.bytedance.myapplication.Repository.ChatRepositoryAI
import com.bytedance.myapplication.Repository.ChatRepositoryHistory
import com.bytedance.myapplication.data.database.AppDatabase
import com.bytedance.myapplication.ui.components.WalkWinApp
import com.bytedance.myapplication.viewmodel.ChatViewModel
import com.bytedance.myapplication.viewmodel.ChatViewModelFactory
import com.bytedance.myapplication.ui.theme.WalkWinTheme

class MainActivity : ComponentActivity() {
    /*使用 lazy 委托来延迟初始化 AppDatabase、ChatRepositoryHistory 和 ChatRepositoryAI。这是一个很好的实践，
    可以避免在 Activity 创建时立即执行耗时操作。*/

    // Get a reference to the AppDatabase
    private val database by lazy { AppDatabase.getDatabase(this) }
    
    // Get a reference to the ChatRepository
    private val repositoryHistory by lazy { ChatRepositoryHistory(database.chatDao()) }
    private val repositoryAI by lazy { ChatRepositoryAI() }

    // Get the ViewModel, passing the factory
    private val viewModel: ChatViewModel by viewModels {
        // For now, we'll start a new chat by passing a null session ID.
        // In a real app, you would pass the ID of a chat to resume.
        ChatViewModelFactory(repositoryAI=repositoryAI, repositoryHistory=repositoryHistory,sessionId = null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WalkWinTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // 使用主题背景色
                ) {
                    WalkWinApp(viewModel)
                }
            }
//            ChatAppTheme {
//                Surface {
//                    ChatScreen(viewModel = viewModel)
//                }
//            }
        }
    }
}