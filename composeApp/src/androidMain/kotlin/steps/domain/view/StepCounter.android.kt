// androidMain/StepCounterAndroid.kt
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import steps.domain.view.StepCounter

class AndroidStepCounter(private val context: Context) : StepCounter, SensorEventListener {
    private var stepCount = 0
    private var sensorManager: SensorManager? = null

    override fun startCounting() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun stopCounting() {
        sensorManager?.unregisterListener(this)
    }

    override fun getStepCount(): Int {
        return stepCount
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            stepCount = event.values[0].toInt()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nicht benötigt für Schrittzählung
    }
}

