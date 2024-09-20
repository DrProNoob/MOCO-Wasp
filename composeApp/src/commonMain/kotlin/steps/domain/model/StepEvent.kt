package steps.domain.model

sealed class StepEvent {
    object StartCounting: StepEvent()
    object StopCounting: StepEvent()
    object StepsCounted: StepEvent()
}