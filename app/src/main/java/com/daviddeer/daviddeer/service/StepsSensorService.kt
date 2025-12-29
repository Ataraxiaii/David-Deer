package com.daviddeer.daviddeer.service

import android.app.*
import android.content.*
import android.content.pm.ServiceInfo
import android.hardware.*
import android.os.*
import androidx.core.app.NotificationCompat
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.util.StepDbHelper
import java.text.SimpleDateFormat
import java.util.*

class StepsSensorService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var dbHelper: StepDbHelper
    private var lastTotalSteps = -1

    override fun onCreate() {
        super.onCreate()
        dbHelper = StepDbHelper(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // 1. Register the step counter sensor
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Null check is recommended as some devices may lack a hardware step counter
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }

        // 2. Start foreground notification
        // Note: Android 14+ requires this to be called immediately upon service start
        startMyForeground()
    }

    private fun startMyForeground() {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "step_channel")
            .setContentTitle("Step Counter Active")
            .setContentText("Walking rewards your beasts!")
            .setSmallIcon(R.drawable.img)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        // Important: Android 14 (API 34) and above mandate specifying the service type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1001, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1001, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
        } else {
            startForeground(1001, notification)
        }
    }



    override fun onSensorChanged(event: SensorEvent) {
        // TYPE_STEP_COUNTER returns total steps since the last device reboot
        val totalStepsSinceBoot = event.values[0].toInt()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (lastTotalSteps != -1) {
            val delta = totalStepsSinceBoot - lastTotalSteps
            if (delta > 0) {
                // Update the database with the new steps increment
                dbHelper.addSteps(today, delta)

                // Notify UI components via broadcast
                // Setting the package name ensures only this app receives the intent
                val intent = Intent("UPDATE_STEP_UI")
                intent.setPackage(packageName)
                sendBroadcast(intent)
            }
        }
        lastTotalSteps = totalStepsSinceBoot
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "step_channel",
                "Counting Steps",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keep tracking your daily steps"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Ensure foreground service is running even if the service is restarted by the system
        startMyForeground()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the listener to conserve battery when the service is stopped
        sensorManager.unregisterListener(this)
    }

    override fun onBind(intent: Intent?) = null
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}