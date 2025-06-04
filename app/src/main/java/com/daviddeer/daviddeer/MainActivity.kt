package com.daviddeer.daviddeer

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import android.widget.ImageButton

//主界面
class MainActivity : ComponentActivity() {
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
    }
}
