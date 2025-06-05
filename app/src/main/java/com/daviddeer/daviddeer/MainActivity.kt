package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.util.LoginManager

//主界面
class MainActivity : ComponentActivity() {
    private val selectBeastLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedBeastImage = result.data?.getIntExtra("selectedBeastImage", 0)
            if (selectedBeastImage != null && selectedBeastImage != 0) {
                findViewById<ImageView>(R.id.mainImage).setImageResource(selectedBeastImage)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 修改这里：使用新的加载方法
        BeastRepository.loadAllStates(this)

        val btnBestiary = findViewById<ImageButton>(R.id.btnBestiary)
        val btnMap = findViewById<ImageButton>(R.id.btnMap)
        val btnGame = findViewById<ImageButton>(R.id.btnGame)
        val btnStep = findViewById<ImageButton>(R.id.btnStep)

        // 跳转图鉴界面
        btnBestiary.setOnClickListener {
            startActivity(Intent(this, BestiaryActivity::class.java))
        }

        // 跳转地图界面
        btnMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // 跳转游戏界面
        btnGame.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        // 跳转计步界面
        btnStep.setOnClickListener {
            startActivity(Intent(this, StepsActivity::class.java))
        }

        // 设置新增按钮的点击事件
        findViewById<ImageButton>(R.id.selectBeastButton).setOnClickListener {
            val intent = Intent(this, SelectBeastActivity::class.java)
            selectBeastLauncher.launch(intent)
        }

        // 说明书跳转
        val btnGuide = findViewById<ImageButton>(R.id.btnGuide)
        btnGuide.setOnClickListener {
            startActivity(Intent(this, GuideActivity::class.java))
        }

        // 设置登出按钮的点击事件
        findViewById<ImageButton>(R.id.btnLogout).setOnClickListener {
            // 登出用户
            LoginManager.logout(this)

            // 保存当前状态（可选，根据你的需求）
            BeastRepository.saveAllStates(this)

            // 显示登出成功消息
            Toast.makeText(this, "已成功登出", Toast.LENGTH_SHORT).show()

            // 返回开始界面并清除所有上层Activity
            val intent = Intent(this, StartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
