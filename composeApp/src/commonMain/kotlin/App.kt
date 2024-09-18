
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chat.view.ChatScreen
import chat.view.ChatViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf
import steps.domain.view.StepScreen
import steps.domain.view.StepViewModel
import steps.domain.view.createStepCounter

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            val navigation = rememberNavController()
//            NavHost(navigation,"chat"){
//                composable("chat") {
//                    val chatViewModel = koinViewModel<ChatViewModel>()
//                    ChatScreen(chatViewModel)
//                }
//            }
            NavHost(navigation,"step"){
                composable("step"){
                    val stepViewModel = koinViewModel<StepViewModel>(){
                        parametersOf(createStepCounter())
                    }
                    StepScreen(stepViewModel)
                }
            }
        }
    }
}