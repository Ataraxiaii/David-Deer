package com.daviddeer.daviddeer.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager


class StepCounterManager(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var listener: ((Int) -> Unit)? = null
    private var isListening = false

    // Check if the device supports the step counter sensor
    fun isStepCounterAvailable(): Boolean {
        return stepSensor != null
    }

    // Start listening for step counts
    fun startListening(listener: (Int) -> Unit) {
        if (!isStepCounterAvailable()) return

        this.listener = listener
        if (!isListening) {
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
            isListening = true
        }
    }

    // Stop listening
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