package com.daviddeer.daviddeer

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GoalSettingActivity : AppCompatActivity() {
    private lateinit var etGoalStep: EditText
    private lateinit var btnSaveGoal: Button
    private val sharedPreferences by lazy {
        getSharedPreferences("StepPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_setting)

        // 设置返回按钮
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        // 初始化视图
        etGoalStep = findViewById(R.id.etGoalStep)
        btnSaveGoal = findViewById(R.id.btnSaveGoal)

        // 显示当前目标
        val currentGoal = sharedPreferences.getInt("step_goal", 10000)
        etGoalStep.setText(currentGoal.toString())
        etGoalStep.setSelection(etGoalStep.text.length) // 将光标放在文本末尾

        // 设置预设目标按钮
        findViewById<Button>(R.id.btnGoal5000).setOnClickListener {
            etGoalStep.setText("5000")
            etGoalStep.setSelection(etGoalStep.text.length)
        }

        findViewById<Button>(R.id.btnGoal10000).setOnClickListener {
            etGoalStep.setText("10000")
            etGoalStep.setSelection(etGoalStep.text.length)
        }

        findViewById<Button>(R.id.btnGoal15000).setOnClickListener {
            etGoalStep.setText("15000")
            etGoalStep.setSelection(etGoalStep.text.length)
        }

        // 添加输入验证
        etGoalStep.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validateInput(s.toString())
            }
        })

        // 保存按钮
        btnSaveGoal.setOnClickListener {
            val newGoalText = etGoalStep.text.toString()

            if (newGoalText.isEmpty()) {
                Toast.makeText(this, "Please enter a valid step goal", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newGoal = newGoalText.toIntOrNull() ?: 0

            if (newGoal <= 0) {
                Toast.makeText(this, "Step goal must be greater than 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sharedPreferences.edit().putInt("step_goal", newGoal).apply()
            Toast.makeText(this, "Goal set to $newGoal steps", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun validateInput(text: String) {
        if (text.isEmpty()) {
            btnSaveGoal.isEnabled = false
            return
        }

        val goal = text.toIntOrNull() ?: 0
        btnSaveGoal.isEnabled = goal > 0
    }
}