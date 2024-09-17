import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import auth.view.AuthView
import auth.view.AuthViewModel
import camera.view.CameraMainView
import camera.view.CameraViewModel
import chat.view.ChatScreen
import chat.view.ChatViewModel
import feed.view.FeedViewModel
import feed.view.MainFeedScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

import mocowasp.composeapp.generated.resources.Res
import mocowasp.composeapp.generated.resources.compose_multiplatform
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            val navigation = rememberNavController()
            NavHost(navigation, "auth") {
                composable("chat") {
                    val chatViewModel = koinViewModel<ChatViewModel>()
                    ChatScreen(chatViewModel)
                }
                composable("auth") {
                    val authViewModel = koinViewModel<AuthViewModel>(){
                        parametersOf(navigation) }
                    AuthView(authViewModel)
                }
                composable("feed") {
                    val feedViewModel = koinViewModel<FeedViewModel>(){
                        parametersOf(navigation) }

                    MainFeedScreen(feedViewModel)
                }
                composable("camera") {
                    val cameraViewModel = koinViewModel<CameraViewModel>(){
                        parametersOf(navigation) }
                    CameraMainView(cameraViewModel)
                }
            }
        }
    }
}