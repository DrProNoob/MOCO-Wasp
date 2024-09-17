package steps.domain.view

interface StepCounter {
    fun startCounting()
    fun stopCounting()
    fun getStepCount(): Int
}
