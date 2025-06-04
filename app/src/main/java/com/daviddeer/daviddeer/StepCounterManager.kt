package com.daviddeer.daviddeer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast

// StepCounterManager.kt
class StepCounterManager(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var stepCount = 0
    private var listener: ((Int) -> Unit)? = null

    // 开始计步
    fun startListening(listener: (Int) -> Unit) {
        this.listener = listener
        if (stepSensor != null) {
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            Toast.makeText(context, "设备不支持计步传感器", Toast.LENGTH_SHORT).show()
        }
    }

    // 停止计步
    fun stopListening() {
        sensorManager.unregisterListener(sensorEventListener)
        listener = null
    }

    // 获取当前步数
    fun getStepCount(): Int = stepCount

    private val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                stepCount = event.values[0].toInt()
                listener?.invoke(stepCount)
            }
        }
    }
}