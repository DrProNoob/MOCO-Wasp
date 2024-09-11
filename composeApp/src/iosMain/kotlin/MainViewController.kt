import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import core.di.initKoin

@Suppress("FunctionName", "unused")
fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
        onFocusBehavior = OnFocusBehavior.DoNothing
    }
) {
    App()
}