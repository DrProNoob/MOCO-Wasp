package steps.domain.view
import org.lighthousegames.logging.logging
import platform.Foundation.*
import platform.HealthKit.*
import kotlin.coroutines.suspendCoroutine

class IOSStepCounter():StepCounter {
    companion object {
        val log = logging()
    }
    private var steps: Int = 0
    var healthStore :HKHealthStore? = null

    override suspend fun startCounting() {
        if(HKsHealthStore.isHealthDataAvailable()){
            healthStore = HKsHealthStore()
            val stepType = HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount)
            requestHealthAuthorization(healthStore!!) { success ->
                if (success) {
                    // 2. Autorisierung erfolgreich, jetzt Schritte abfragen
                    val now = NSDate()
                    val startOfDay = NSCalendar.currentCalendar.startOfDayForDate(now)
                    val predicate = HKQuery.predicateForSamplesWithStartDate(startOfDay, endDate = now, options = 0)

                    val query = HKStatisticsQuery(
                        quantityType = stepType,
                        quantitySamplePredicate = predicate,
                        options = HKStatisticsOptionCumulativeSum
                    ) { _, result, error ->
                        if (error != null) {
                            // Fehlerbehandlung, wenn die Abfrage fehlschlägt
                            log.i{"Error fetching steps: ${error.localizedDescription}"}
                        } else {
                            val sumQuantity = result?.sumQuantity()
                            steps = sumQuantity?.doubleValueForUnit(HKUnit.count())?.toInt() ?: 0
                        }
                    }
                    // 3. Führe die Abfrage aus
                    healthStore!!.executeQuery(query)
                } else {
                    // Autorisierung fehlgeschlagen
                    log.i{"Authorization failed"}
                }
            }
        } else {
            log.i{"Health data is not available on this device"}
        }
    }


    fun requestHealthAuthorization(healthStore: HKHealthStore, onAuthorizationComplete: (Boolean) -> Unit) {
        val stepType = HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount)

        healthStore.requestAuthorizationToShareTypes(null, readTypes = setOf(stepType)) { success, error ->
            if (success) {
                onAuthorizationComplete(true)
            } else {
                onAuthorizationComplete(false)
            }
        }
    }


    override fun stopCounting() {
        // Nicht benötigt
    }

    override fun getStepCount(): Int {
        return steps
    }

}
