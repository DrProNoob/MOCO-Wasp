package steps.domain.view

interface StepCounter {
    suspend fun startCounting()
    fun stopCounting()
    fun getStepCount(): Int
}
