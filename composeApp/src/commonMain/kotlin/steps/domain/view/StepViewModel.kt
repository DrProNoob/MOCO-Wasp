package steps.domain.view

import androidx.lifecycle.ViewModel
import org.lighthousegames.logging.logging

class StepViewModel(): ViewModel() {
    companion object {
        val log = logging()
    }

    private val stepCounter: StepCounter = createStepCounter()

    fun startStepCounting() {
        stepCounter.startCounting()
    }

    fun stopStepCounting() {
        stepCounter.stopCounting()
    }

    fun getStepCount(): Int {
        return stepCounter.getStepCount()
    }
}