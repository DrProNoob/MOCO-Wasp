// androidMain/StepCounterAndroid.kt
import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.suspendCancellableCoroutine
import org.lighthousegames.logging.Log
import org.lighthousegames.logging.logging
import steps.domain.view.StepCounter
import kotlin.coroutines.resume
import android.os.Handler
import android.os.Looper

class AndroidStepCounter(private val context: Context) : StepCounter, SensorEventListener {
    private var stepCount = 0
    private var sensorManager: SensorManager? = null

    private val handler = Handler(Looper.getMainLooper())
    companion object {
        val log = logging()
    }

    override fun startCounting() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            log.i{ "Sensor is null"}
        }else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)

        }
        handler.postDelayed(object : Runnable {
            override fun run() {
                Log.d("MainActivity", "Schritte seit Neustart: $stepCount")
                // Wiederhole die Abfrage nach 15 Sekunden
                handler.postDelayed(this, 15000)
            }
        }, 15000)


    }
    suspend fun steps() = suspendCancellableCoroutine { continuation ->
        Log.d(TAG, "Registering sensor listener... ")


        val listener: SensorEventListener by lazy {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return

                    stepCount = event.values[0].toInt()
                    Log.d(TAG, "Steps since last reboot: $stepCount")

                    if (continuation.isActive) {
                        continuation.resume(stepCount)
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    Log.d(TAG, "Accuracy changed to: $accuracy")
                }
            }
        }
 }
    override fun stopCounting() {
        sensorManager?.unregisterListener(this)
    }

    override fun getStepCount(): Int {
        return stepCount
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            stepCount = event.values[0].toInt()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nicht benötigt für Schrittzählung
    }
}

