package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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


// 第二关是快速反应点击游戏 反应时间1秒，10分通关 玩家要在13秒内完成游戏，否则失败
class LevelTwoActivity : ComponentActivity() {
    private lateinit var target: ImageView
    private lateinit var tvScore: TextView
    private var score = 0
    private val maxScore = 10
    private var gameActive = true
    private var gameTimer: CountDownTimer? = null // 游戏计时器

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
            gameTimer?.cancel()  // 停止游戏计时
            gameActive = false   // 终止游戏状态
            finish()             // 返回上一界面（GameActivity）
        }

        btnStart.visibility = View.VISIBLE  // 重新进入页面时按钮显示

        // 开始游戏按钮
        btnStart.setOnClickListener {
            btnStart.visibility = View.GONE  // 点击后隐藏按钮
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
                    }, 10)
                }
            }
        }
    }

    // 开始游戏
    private fun startGame() {
        score = 0
        gameActive = true
        tvScore.text = "Score: 0/$maxScore"

        // 启动13秒倒计时
        gameTimer?.cancel()
        gameTimer = object : CountDownTimer(13000, 1000) {
            val tvTimer = findViewById<TextView>(R.id.tvTimer)
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = "Time: ${millisUntilFinished / 1000}s"
            } // 剩余时间

            override fun onFinish() {
                if (gameActive) {
                    gameActive = false
                    onGameFailed()
                }
            }
        }.start()

        showRandomTarget()
    }

    // 游戏失败
    private fun onGameFailed() {
        Toast.makeText(this, "Time's up! Try again!", Toast.LENGTH_SHORT).show()
        gameTimer?.cancel()

        // 返回关卡选择界面（GameActivity）
        val intent = Intent(this, GameActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
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

        // 1 秒后自动消失
        Handler(Looper.getMainLooper()).postDelayed({
            if (target.visibility == View.VISIBLE) {
                target.visibility = View.INVISIBLE
                showRandomTarget()
            }
        }, 1000)
    }

    // 通关解锁
    private fun onLevelTwoPassed() {
        gameTimer?.cancel()
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
