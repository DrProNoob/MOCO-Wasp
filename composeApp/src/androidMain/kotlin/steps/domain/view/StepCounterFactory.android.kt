package steps.domain.view
import AndroidStepCounter
import android.content.Context

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual fun createStepCounter(context: Context): StepCounter {
    return AndroidStepCounter(context)
}