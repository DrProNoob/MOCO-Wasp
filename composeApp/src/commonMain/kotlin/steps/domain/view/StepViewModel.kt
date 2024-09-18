package steps.domain.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

class StepViewModel(private val stepCounter: StepCounter) : ViewModel() {
    companion object {
        val log = logging()
    }

    private val _counterState = MutableStateFlow(stepCounter.getStepCount())
    val counterState = _counterState.asStateFlow()

    init {
        viewModelScope.launch {
            startStepCounting()
            log.i{counterState.value}
            _counterState.update { stepCounter.getStepCount() }
        }
    }

    fun startStepCounting() {
        viewModelScope.launch {
            stepCounter.startCounting()
        }
    }

    fun stopStepCounting() {
        stepCounter.stopCounting()
    }

    fun getStepCount(): Int {
        return stepCounter.getStepCount()
    }
}
