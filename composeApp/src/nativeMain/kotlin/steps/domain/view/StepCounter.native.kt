// iosMain/StepCounterIOS.kt
import platform.Foundation.*
import steps.domain.view.StepCounter

class IOSStepCounter : StepCounter {
    private val iosStepCounter = IOSStepCounter()

    override fun startCounting() {
        iosStepCounter.startCounting()
    }

    override fun stopCounting() {
        iosStepCounter.stopCounting()
    }

    override fun getStepCount(): Int {
        return iosStepCounter.getStepCount()
    }
}

