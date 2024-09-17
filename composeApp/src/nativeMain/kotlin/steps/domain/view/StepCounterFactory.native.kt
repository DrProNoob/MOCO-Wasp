package steps.domain.view

actual fun createStepCounter(): StepCounter {
    return IOSStepCounter()
}

actual object AppContext