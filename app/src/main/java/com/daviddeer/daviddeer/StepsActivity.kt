package com.daviddeer.daviddeer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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
    // 使用Locale.ENGLISH确保显示英文
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
    private val weekdayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
    private var initialStepCount = 0 // 记录初始步数

    // 注册 Activity Result 回调
    private val goalSettingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // 目标更新，重新加载
            stepGoal = sharedPreferences.getInt("step_goal", 10000)
            goalTextView.text = "Goal: $stepGoal steps"
            progressBar.max = stepGoal
        }
    }

    // 注册权限请求回调
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("StepsActivity", "计步权限已授予")
            startStepCounter()
        } else {
            Toast.makeText(this, "需要计步权限才能使用此功能", Toast.LENGTH_SHORT).show()
            finish() // 如果用户拒绝，关闭Activity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        // 初始化视图
        try {
            stepCountTextView = findViewById(R.id.stepCountTextView)
            goalTextView = findViewById(R.id.goalTextView)
            calorieTextView = findViewById(R.id.calorieTextView)
            progressBar = findViewById(R.id.progressBar)
            historyContainer = findViewById(R.id.historyContainer)
        } catch (e: Exception) {
            Log.e("StepsActivity", "初始化视图失败: ${e.message}")
            Toast.makeText(this, "加载界面失败，请重试", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 检查系统时间
        checkSystemTime()

        // 初始化步数管理器
        stepCounterManager = StepCounterManager(this)

        // 设置按钮
        findViewById<ImageView>(R.id.btnSetGoal).setOnClickListener {
            goalSettingLauncher.launch(Intent(this, GoalSettingActivity::class.java))
        }

        // 返回按钮
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressed() // 使用系统默认返回逻辑
        }

        // 加载历史记录
        loadHistoryRecords()
    }

    override fun onResume() {
        super.onResume()

        // 加载目标步数
        stepGoal = sharedPreferences.getInt("step_goal", 10000)
        goalTextView.text = "Goal: $stepGoal steps"
        progressBar.max = stepGoal

        // 检查传感器可用性
        if (!stepCounterManager.isStepCounterAvailable()) {
            Toast.makeText(this, "您的设备不支持计步功能", Toast.LENGTH_LONG).show()
            return
        }

        // 检查并请求权限
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
            // 开始监听步数
            stepCounterManager.startListening { stepCount ->
                if (initialStepCount == 0) {
                    // 首次获取步数，设置初始值
                    initialStepCount = stepCount
                    Log.d("StepsActivity", "初始步数: $initialStepCount")
                }

                // 计算今日步数（传感器累计步数 - 初始步数）
                val todaySteps = max(0, stepCount - initialStepCount)

                runOnUiThread {
                    updateStepDisplay(todaySteps)
                    saveTodaySteps(todaySteps)
                }
            }
        } catch (e: Exception) {
            Log.e("StepsActivity", "启动计步器失败: ${e.message}")
            Toast.makeText(this, "计步功能暂时不可用", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            stepCounterManager.stopListening()
        } catch (e: Exception) {
            Log.e("StepsActivity", "停止计步器失败: ${e.message}")
        }
    }

    private fun updateStepDisplay(stepCount: Int) {
        try {
            stepCountTextView.text = stepCount.toString()
            progressBar.progress = min(stepCount, stepGoal)

            // 计算卡路里
            val calories = (stepCount * 0.04).toInt()
            calorieTextView.text = "Burned: $calories kcal"

            // 动态更新进度条颜色
            val progressPercent = (stepCount.toFloat() / stepGoal * 100).toInt()
            updateProgressBarColor(progressPercent)
        } catch (e: Exception) {
            Log.e("StepsActivity", "更新步数显示失败: ${e.message}")
        }
    }

    private fun updateProgressBarColor(progressPercent: Int) {
        try {
            // 修复进度条颜色资源引用
            val drawableRes = when {
                progressPercent >= 100 -> R.drawable.progress_bar_complete
                progressPercent >= 75 -> R.drawable.progress_bar_normal
                progressPercent >= 50 -> R.drawable.progress_bar_normal
                else -> R.drawable.progress_bar_low
            }

            progressBar.progressDrawable = getDrawable(drawableRes)
        } catch (e: Exception) {
            Log.e("StepsActivity", "更新进度条颜色失败: ${e.message}")
            // 使用默认颜色
            progressBar.progressDrawable = getDrawable(android.R.drawable.progress_horizontal)
        }
    }

    private fun saveTodaySteps(stepCount: Int) {
        try {
            val today = getSafeDateString()
            sharedPreferences.edit().putInt("steps_$today", stepCount).apply()
            Log.d("StepsActivity", "保存今日步数: $stepCount for $today")
        } catch (e: Exception) {
            Log.e("StepsActivity", "保存步数失败: ${e.message}")
        }
    }

    private fun loadHistoryRecords() {
        try {
            historyContainer.removeAllViews()
            val today = getSafeDateString() // 获取今天的日期

            // 获取最近7天的历史记录
            val calendar = Calendar.getInstance()
            for (i in 0 until 7) {
                val dateStr = getSafeDateString(calendar)
                val weekdayStr = weekdayFormat.format(calendar.time)

                // 跳过今天的记录
                if (dateStr == today) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                    continue
                }

                val steps = sharedPreferences.getInt("steps_$dateStr", 0)
                if (steps > 0) { // 只显示有步数记录的日期
                    addHistoryItem(dateStr, weekdayStr, steps)
                }

                calendar.add(Calendar.DAY_OF_MONTH, -1)
            }
        } catch (e: Exception) {
            Log.e("StepsActivity", "加载历史记录失败: ${e.message}")
        }
    }

    private fun addHistoryItem(date: String, weekday: String, steps: Int) {
        try {
            val historyItem = LayoutInflater.from(this)
                .inflate(R.layout.history_item, historyContainer, false)

            historyItem.findViewById<TextView>(R.id.dateTextView).text = date
            historyItem.findViewById<TextView>(R.id.weekdayTextView).text = weekday
            historyItem.findViewById<TextView>(R.id.stepsTextView).text = "$steps steps"

            historyItem.setOnClickListener {
                // 可以实现历史详情查看功能
            }

            historyContainer.addView(historyItem)
        } catch (e: Exception) {
            Log.e("StepsActivity", "添加历史记录项失败: ${e.message}")
        }
    }

    // 安全获取日期字符串，处理系统时间异常
    private fun getSafeDateString(calendar: Calendar = Calendar.getInstance()): String {
        return try {
            dateFormat.format(calendar.time)
        } catch (e: Exception) {
            Log.e("StepsActivity", "日期格式化失败: ${e.message}")
            // 返回一个默认日期或时间戳作为后备
            "Unknown Date ${System.currentTimeMillis()}"
        }
    }

    // 检查系统时间是否合理
    private fun checkSystemTime() {
        try {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            if (currentYear < 2020 || currentYear > 2030) { // 设置合理范围
                Toast.makeText(
                    this,
                    "系统时间异常，请在设置中调整日期和时间",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Log.e("StepsActivity", "系统时间检查失败: ${e.message}")
        }
    }
}