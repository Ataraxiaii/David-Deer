package com.daviddeer.daviddeer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast

// StepCounterManager.kt
class StepCounterManager(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var listener: ((Int) -> Unit)? = null
    private var isListening = false

    // 检查设备是否支持计步传感器
    fun isStepCounterAvailable(): Boolean {
        return stepSensor != null
    }

    // 开始监听步数
    fun startListening(listener: (Int) -> Unit) {
        if (!isStepCounterAvailable()) return

        this.listener = listener
        if (!isListening) {
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
            isListening = true
        }
    }

    // 停止监听
    fun stopListening() {
        if (isListening) {
            sensorManager.unregisterListener(sensorEventListener)
            isListening = false
        }
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                val stepCount = event.values[0].toInt()
                listener?.invoke(stepCount)
            }
        }
    }
}