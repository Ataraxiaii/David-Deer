package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.R
import kotlin.random.Random


// 第二关是快速反应点击游戏 反应时间0.5秒，10分通关
class LevelTwoActivity : ComponentActivity() {
    private lateinit var target: ImageView
    private lateinit var tvScore: TextView
    private var score = 0
    private val maxScore = 10
    private var gameActive = true

    // 初始化灵兽图片
    private val beastImages = listOf(
        R.drawable.qilin,
        R.drawable.whitetiger,
        R.drawable.xvanwu,
        R.drawable.yinglong
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_two)

        val btnStart = findViewById<Button>(R.id.btnStart)
        tvScore = findViewById(R.id.tvScore)
        target = findViewById(R.id.target)
        target.visibility = View.INVISIBLE

        // 返回选择关卡界面
        val backButton = findViewById<ImageButton>(R.id.btnBackGamePage)
        backButton.setOnClickListener {
            finish()  // 返回上一界面（GameActivity）
        }

        // 开始游戏按钮
        btnStart.setOnClickListener {
            startGame()
        }

        // 点击图片的监听
        target.setOnClickListener {
            if (gameActive) {
                score++
                tvScore.text = "Score: $score/$maxScore"
                target.visibility = View.INVISIBLE

                if (score >= maxScore) {
                    gameActive = false
                    onLevelTwoPassed()
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        showRandomTarget()
                    }, 300)
                }
            }
        }
    }

    // 开始游戏
    private fun startGame() {
        score = 0
        gameActive = true
        tvScore.text = "Score: 0/$maxScore"
        showRandomTarget()
    }

    // 出现的灵兽图片
    private fun showRandomTarget() {
        if (!gameActive) return

        val screenWidth = resources.displayMetrics.widthPixels - 200
        val screenHeight = resources.displayMetrics.heightPixels
        val upperPadding = 300 // 大约是按钮 + 分数区域以下
        val lowerPadding = 300 // 避免底部遮挡
        val availableHeight = screenHeight - upperPadding - lowerPadding
        val x = Random.nextInt(screenWidth)
        val y = upperPadding + Random.nextInt(availableHeight)

        target.setImageResource(beastImages.random()) // 随机选择一个灵兽图
        target.x = x.toFloat()
        target.y = y.toFloat()
        target.visibility = View.VISIBLE

        // 0.5 秒后自动消失
        Handler(Looper.getMainLooper()).postDelayed({
            if (target.visibility == View.VISIBLE) {
                target.visibility = View.INVISIBLE
                showRandomTarget()
            }
        }, 500)
    }

    // 通关解锁
    private fun onLevelTwoPassed() {
        // 提示通关
        Toast.makeText(this, "Successfully passed the level 2!\n4  new beasts have been unlocked.", Toast.LENGTH_SHORT).show()
        // 解锁10~13 的灵兽
        BeastRepository.unlockBeastsByIds(listOf(10, 11, 12, 13))
        // 每次通关后保存解锁状态
        BeastRepository.saveUnlockedState(this)

        // 返回GameActivity
        val intent = Intent(this, GameActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 清除返回栈
        startActivity(intent)
        finish()
    }
}
