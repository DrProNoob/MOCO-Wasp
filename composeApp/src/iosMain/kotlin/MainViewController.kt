import androidx.compose.ui.window.ComposeUIViewController
import camera.di.initCameraKoin

@Suppress("FunctionName", "unused")
fun MainViewController() = ComposeUIViewController(
    configure = {
        initCameraKoin()
    }
) {
    App()
}