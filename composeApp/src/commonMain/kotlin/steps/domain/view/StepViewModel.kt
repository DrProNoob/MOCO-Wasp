package steps.domain.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import steps.domain.model.StepEvent

class StepViewModel(private val stepCounter: StepCounter) : ViewModel() {
    companion object {
        val log = logging()
    }
    private var job: Job? = null



    private val _counterState = MutableStateFlow(stepCounter.getStepCount())
    val counterState = _counterState.asStateFlow()
    var startValueOfCountedSteps = 0

    init {
        viewModelScope.launch {
            stepCounter.startCounting()
            delay(2000)
            startValueOfCountedSteps = stepCounter.getStepCount()

        }
    }

    fun onEvent(event: StepEvent) {
        when (event) {
            is StepEvent.StartCounting -> startStepCounting()
            is StepEvent.StopCounting -> stopStepCounting()
            is StepEvent.StepsCounted -> {
                _counterState.value = stepCounter.getStepCount()
                _counterState.update { stepCounter.getStepCount() }
            }
        }
    }

    fun startStepCounting() {
        job = viewModelScope.launch {
            stepCounter.startCounting()
            while (true) {
                _counterState.update { stepCounter.getStepCount()-startValueOfCountedSteps }
                delay(2000)  // Aktualisiere alle 2 Sekunden
            }
        }
    }

    fun stopStepCounting() {
        stepCounter.stopCounting()
        job?.cancel()
    }

    fun getStepCount(): Int {
        return stepCounter.getStepCount()
    }


}
