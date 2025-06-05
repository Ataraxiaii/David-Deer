package com.daviddeer.daviddeer.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.util.StepCounterManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class StepsActivity : AppCompatActivity() {
    private lateinit var stepCounterManager: StepCounterManager
    private lateinit var stepCountTextView: TextView
    private lateinit var goalTextView: TextView
    private lateinit var calorieTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var historyContainer: LinearLayout
    private var stepGoal = 10000
    private val sharedPreferences by lazy {
        getSharedPreferences("StepPrefs", Context.MODE_PRIVATE)
    }

    // Use Locale.ENGLISH to ensure English display
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
    private val weekdayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)

    // Store initial step count and current date
    private val PREFS_KEY_INITIAL_STEPS = "initial_steps"
    private val PREFS_KEY_CURRENT_DATE = "current_date"

    // Current date
    private var currentDate: String = ""

    private val goalSettingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Update goal and reload
            stepGoal = sharedPreferences.getInt("step_goal", 10000)
            goalTextView.text = "Goal: $stepGoal steps"
            progressBar.max = stepGoal
        }
    }

    // Register permission request callback
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("StepsActivity", "Step counting permission granted")
            startStepCounter()
        } else {
            Toast.makeText(this, "Step counting permission is required to use this feature", Toast.LENGTH_SHORT).show()
            finish() // Close activity if permission is denied
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        try {
            stepCountTextView = findViewById(R.id.stepCountTextView)
            goalTextView = findViewById(R.id.goalTextView)
            calorieTextView = findViewById(R.id.calorieTextView)
            progressBar = findViewById(R.id.progressBar)
            historyContainer = findViewById(R.id.historyContainer)
        } catch (e: Exception) {
            Log.e("StepsActivity", "View initialization failed: ${e.message}")
            Toast.makeText(this, "Failed to load interface, please try again", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize current date
        currentDate = getSafeDateString()

        // Initialize step counter manager
        stepCounterManager = StepCounterManager(this)

        // Check for date change and reset step count if needed
        checkDateChange()

        // Check system time validity
        checkSystemTime()

        // Set button click listeners
        findViewById<ImageView>(R.id.btnSetGoal).setOnClickListener {
            goalSettingLauncher.launch(Intent(this, GoalSettingActivity::class.java))
        }

        // Back button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }

        // Load history records
        loadHistoryRecords()
    }

    override fun onResume() {
        super.onResume()

        // Update current date
        currentDate = getSafeDateString()
        checkDateChange()

        // Load step goal
        stepGoal = sharedPreferences.getInt("step_goal", 10000)
        goalTextView.text = "Goal: $stepGoal steps"
        progressBar.max = stepGoal

        // Check sensor availability
        if (!stepCounterManager.isStepCounterAvailable()) {
            Toast.makeText(this, "Your device does not support step counting", Toast.LENGTH_LONG).show()
            return
        }

        // Check and request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            } else {
                startStepCounter()
            }
        } else {
            startStepCounter()
        }
    }

    private fun startStepCounter() {
        try {
            stepCounterManager.startListening { stepCount ->
                // Get stored initial step count
                val storedInitialSteps = sharedPreferences.getInt(PREFS_KEY_INITIAL_STEPS, -1)

                if (storedInitialSteps == -1) {
                    // Set initial steps on first launch or device reboot
                    sharedPreferences.edit()
                        .putInt(PREFS_KEY_INITIAL_STEPS, stepCount)
                        .putString(PREFS_KEY_CURRENT_DATE, currentDate)
                        .apply()

                    Log.d("StepsActivity", "Initial steps set: $stepCount")
                }

                // Calculate today's steps
                val todaySteps = max(0, stepCount - storedInitialSteps)

                runOnUiThread {
                    updateStepDisplay(todaySteps)
                    saveTodaySteps(todaySteps)
                }
            }
        } catch (e: Exception) {
            Log.e("StepsActivity", "Step counter startup failed: ${e.message}")
            Toast.makeText(this, "Step counting function is temporarily unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    // Check for date change
    private fun checkDateChange() {
        val savedDate = sharedPreferences.getString(PREFS_KEY_CURRENT_DATE, "")

        if (savedDate.isNullOrEmpty() || savedDate != currentDate) {
            // Date changed, reset initial steps
            sharedPreferences.edit()
                .remove(PREFS_KEY_INITIAL_STEPS)
                .putString(PREFS_KEY_CURRENT_DATE, currentDate)
                .apply()

            Log.d("StepsActivity", "Date changed, reset initial steps")

            // Restart step counter to get new initial value if already running
            if (stepCounterManager.isStepCounterAvailable()) {
                stepCounterManager.stopListening()
                startStepCounter()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            stepCounterManager.stopListening()
        } catch (e: Exception) {
            Log.e("StepsActivity", "Step counter stop failed: ${e.message}")
        }
    }

    private fun updateStepDisplay(stepCount: Int) {
        try {
            stepCountTextView.text = stepCount.toString()
            progressBar.progress = min(stepCount, stepGoal)

            // Calculate calories burned
            val calories = (stepCount * 0.04).toInt()
            calorieTextView.text = "Burned: $calories kcal"

            // Dynamically update progress bar color
            val progressPercent = (stepCount.toFloat() / stepGoal * 100).toInt()
            updateProgressBarColor(progressPercent)
        } catch (e: Exception) {
            Log.e("StepsActivity", "Step display update failed: ${e.message}")
        }
    }

    private fun updateProgressBarColor(progressPercent: Int) {
        try {
            // Fix progress bar color resource reference
            val drawableRes = when {
                progressPercent >= 100 -> R.drawable.progress_bar_complete
                progressPercent >= 75 -> R.drawable.progress_bar_normal
                progressPercent >= 50 -> R.drawable.progress_bar_normal
                else -> R.drawable.progress_bar_low
            }

            progressBar.progressDrawable = getDrawable(drawableRes)
        } catch (e: Exception) {
            Log.e("StepsActivity", "Progress bar color update failed: ${e.message}")
            // Use default color
            progressBar.progressDrawable = getDrawable(android.R.drawable.progress_horizontal)
        }
    }

    private fun saveTodaySteps(stepCount: Int) {
        try {
            val today = getSafeDateString()
            sharedPreferences.edit().putInt("steps_$today", stepCount).apply()
            Log.d("StepsActivity", "Saved today's steps: $stepCount for $today")
        } catch (e: Exception) {
            Log.e("StepsActivity", "Step saving failed: ${e.message}")
        }
    }

    private fun loadHistoryRecords() {
        try {
            historyContainer.removeAllViews()
            val today = getSafeDateString() // Get today's date

            // Get last 7 days of history
            val calendar = Calendar.getInstance()
            for (i in 0 until 7) {
                val dateStr = getSafeDateString(calendar)
                val weekdayStr = weekdayFormat.format(calendar.time)

                // Skip today's record
                if (dateStr == today) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                    continue
                }

                val steps = sharedPreferences.getInt("steps_$dateStr", 0)
                if (steps > 0) { // Only show dates with recorded steps
                    addHistoryItem(dateStr, weekdayStr, steps)
                }

                calendar.add(Calendar.DAY_OF_MONTH, -1)
            }
        } catch (e: Exception) {
            Log.e("StepsActivity", "History record loading failed: ${e.message}")
        }
    }

    private fun addHistoryItem(date: String, weekday: String, steps: Int) {
        try {
            val historyItem = LayoutInflater.from(this)
                .inflate(R.layout.history_item, historyContainer, false)

            historyItem.findViewById<TextView>(R.id.dateTextView).text = date
            historyItem.findViewById<TextView>(R.id.weekdayTextView).text = weekday
            historyItem.findViewById<TextView>(R.id.stepsTextView).text = "$steps steps"

            historyContainer.addView(historyItem)
        } catch (e: Exception) {
            Log.e("StepsActivity", "History item addition failed: ${e.message}")
        }
    }

    // Safely get date string, handle system time exceptions
    private fun getSafeDateString(calendar: Calendar = Calendar.getInstance()): String {
        return try {
            dateFormat.format(calendar.time)
        } catch (e: Exception) {
            Log.e("StepsActivity", "Date formatting failed: ${e.message}")
            // Return default date or timestamp as fallback
            "Unknown Date ${System.currentTimeMillis()}"
        }
    }

    // Check if system time is reasonable
    private fun checkSystemTime() {
        try {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            if (currentYear < 2020 || currentYear > 2030) { // Set reasonable range
                Toast.makeText(
                    this,
                    "System time is abnormal, please adjust date and time in settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Log.e("StepsActivity", "System time check failed: ${e.message}")
        }
    }
}