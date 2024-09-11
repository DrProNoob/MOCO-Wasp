
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chat.view.ChatScreen
import chat.view.ChatViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

import mocowasp.composeapp.generated.resources.Res
import mocowasp.composeapp.generated.resources.compose_multiplatform
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            val navigation = rememberNavController()
            NavHost(navigation,"chat"){
                composable("chat") {
                    val chatViewModel = koinViewModel<ChatViewModel>()
                    ChatScreen(chatViewModel)
                }
            }
        }
    }
}