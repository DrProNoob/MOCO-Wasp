package steps.domain.model

sealed class StepEvent {
    data class StartCounting(val stepGoal:Int): StepEvent()
    object StopCounting: StepEvent()
    object StepsCounted: StepEvent()
}