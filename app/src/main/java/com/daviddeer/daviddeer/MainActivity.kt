package com.daviddeer.daviddeer

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts

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
        findViewById<Button>(R.id.selectBeastButton).setOnClickListener {
            val intent = Intent(this, SelectBeastActivity::class.java)
            selectBeastLauncher.launch(intent)
        }

        // 说明书跳转
        val btnGuide = findViewById<ImageButton>(R.id.btnGuide)
        btnGuide.setOnClickListener {
            startActivity(Intent(this, GuideActivity::class.java))
        }
    }
}
