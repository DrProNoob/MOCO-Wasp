
import steps.domain.view.StepCounter

class IOSStepCounter : StepCounter {
    private val iosStepCounter = IOSStepCounter()

    override suspend fun startCounting() {
        iosStepCounter.startCounting()
    }

    override fun stopCounting() {
        iosStepCounter.stopCounting()
    }

    override fun getStepCount(): Int {
        return iosStepCounter.getStepCount()
    }
}

