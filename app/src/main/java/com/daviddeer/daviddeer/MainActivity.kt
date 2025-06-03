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

        btnBestiary.setOnClickListener {
            startActivity(Intent(this, BestiaryActivity::class.java))
        }

        btnMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        btnGame.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }
}
