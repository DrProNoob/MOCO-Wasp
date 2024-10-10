package steps.domain.view
import AndroidStepCounter
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.lang.ref.WeakReference

//@Suppress("ACTUAL_WITHOUT_EXPECT")
actual fun createStepCounter(): StepCounter {
    val context = AppContext.get()
    return AndroidStepCounter(context)

}
actual object AppContext {
    private var value: WeakReference<Context?>? = null
    fun set(context: Context) {
        value = WeakReference(context)
    }
    internal fun get(): Context {
        return value?.get() ?: throw RuntimeException("Context Error")
    }
}

