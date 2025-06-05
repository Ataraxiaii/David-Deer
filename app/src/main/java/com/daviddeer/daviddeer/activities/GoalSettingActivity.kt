package com.daviddeer.daviddeer.activities

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daviddeer.daviddeer.R

/**
 * Step Goal Setting Activity
 * Function: Allows users to set daily step goals with preset options
 */
class GoalSettingActivity : AppCompatActivity() {
    // Step goal input field
    private lateinit var etGoalStep: EditText
    // Save button
    private lateinit var btnSaveGoal: Button
    // Initialize SharedPreferences for persistent step goal storage
    private val sharedPreferences by lazy {
        getSharedPreferences("StepPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_setting)

        /* ==================== 1. Initialize UI Components ==================== */
        // Set back button click listener
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        etGoalStep = findViewById(R.id.etGoalStep)  // Step goal input field
        btnSaveGoal = findViewById(R.id.btnSaveGoal) // Save button

        /* ==================== 2. Display Current Goal Value ==================== */
        // Read current goal from SharedPreferences (default: 10000 steps)
        val currentGoal = sharedPreferences.getInt("step_goal", 10000)
        etGoalStep.setText(currentGoal.toString())
        etGoalStep.setSelection(etGoalStep.text.length) // Move cursor to end

        /* ==================== 3. Preset Button Configuration ==================== */
        // 5000 steps preset button
        findViewById<Button>(R.id.btnGoal5000).setOnClickListener {
            etGoalStep.setText("5000")
            etGoalStep.setSelection(etGoalStep.text.length)
        }

        // 10000 steps preset button (default)
        findViewById<Button>(R.id.btnGoal10000).setOnClickListener {
            etGoalStep.setText("10000")
            etGoalStep.setSelection(etGoalStep.text.length)
        }

        // 15000 steps preset button
        findViewById<Button>(R.id.btnGoal15000).setOnClickListener {
            etGoalStep.setText("15000")
            etGoalStep.setSelection(etGoalStep.text.length)
        }

        /* ==================== 4. Input Validation Logic ==================== */
        // Add text change listener to input field
        etGoalStep.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Validate input content
                validateInput(s.toString())
            }
        })

        /* ==================== 5. Save Button Logic ==================== */
        btnSaveGoal.setOnClickListener {
            val newGoalText = etGoalStep.text.toString()

            // Handle empty input
            if (newGoalText.isEmpty()) {
                Toast.makeText(this, "Please enter a valid step goal", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Try to convert to integer, default to 0 if failed
            val newGoal = newGoalText.toIntOrNull() ?: 0

            // Verify goal value is greater than 0
            if (newGoal <= 0) {
                Toast.makeText(this, "Step goal must be greater than 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to SharedPreferences
            sharedPreferences.edit().putInt("step_goal", newGoal).apply()
            Toast.makeText(this, "Goal set to $newGoal steps", Toast.LENGTH_SHORT).show()

            finish()
        }
    }

    /**
     * Validate user input
     * @param text User input text
     */
    private fun validateInput(text: String) {
        // Disable save button if input is empty
        if (text.isEmpty()) {
            btnSaveGoal.isEnabled = false
            return
        }
        val goal = text.toIntOrNull() ?: 0
        btnSaveGoal.isEnabled = goal > 0
    }
}