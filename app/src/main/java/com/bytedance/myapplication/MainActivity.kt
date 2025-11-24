// com/bytedance/myapplication/MainActivity.kt

package com.bytedance.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import com.bytedance.myapplication.Repository.ChatRepositoryAI
import com.bytedance.myapplication.Repository.ChatRepositoryHistory
import com.bytedance.myapplication.data.database.AppDatabase
import com.bytedance.myapplication.ui.ChatScreen
//import com.bytedance.myapplication.ui.chat.ChatScreen
import com.bytedance.myapplication.viewmodel.ChatViewModel
import com.bytedance.myapplication.viewmodel.ChatViewModelFactory
import com.bytedance.myapplication.ui.theme.ChatAppTheme

class MainActivity : ComponentActivity() {

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
            ChatAppTheme {
                Surface {
                    ChatScreen(viewModel = viewModel)
                }
            }
        }
    }
}