package com.daviddeer.daviddeer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlin.math.min

// StepsActivity.kt
class StepsActivity : ComponentActivity() {
    private lateinit var stepCounterManager: StepCounterManager
    private lateinit var stepCountTextView: TextView
    private lateinit var goalTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var stepGoal = 10000 // 改为 var，允许修改

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        stepCounterManager = StepCounterManager(this)
        stepCountTextView = findViewById(R.id.stepCountTextView)
        goalTextView = findViewById(R.id.goalTextView)
        progressBar = findViewById(R.id.progressBar)

        // 设置按钮
        val btnSetGoal = findViewById<ImageButton>(R.id.btnSetGoal)
        btnSetGoal.setOnClickListener {
            startActivity(Intent(this, GoalSettingActivity::class.java))
        }

        // 返回按钮
        val backButton = findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        // 重新加载目标步数
        val sharedPreferences = getSharedPreferences("StepPrefs", Context.MODE_PRIVATE)
        stepGoal = sharedPreferences.getInt("step_goal", 10000)
        goalTextView.text = "目标: $stepGoal 步"
        progressBar.max = stepGoal // 更新进度条最大值

        // 开始监听步数
        stepCounterManager.startListening { stepCount ->
            runOnUiThread {
                updateStepDisplay(stepCount)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stepCounterManager.stopListening()
    }

    private fun updateStepDisplay(stepCount: Int) {
        stepCountTextView.text = "今日步数: $stepCount"
        val displayProgress = min(stepCount, stepGoal)
        progressBar.progress = displayProgress

        if (stepCount >= stepGoal) {
            progressBar.progressDrawable = getDrawable(R.drawable.progress_bar_complete)
        } else {
            progressBar.progressDrawable = getDrawable(R.drawable.progress_bar_normal)
        }
    }
}