package com.daviddeer.daviddeer

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.content.Intent


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnBestiary = findViewById<Button>(R.id.btnBestiary)
        val btnMap = findViewById<Button>(R.id.btnMap)
        val btnGame = findViewById<Button>(R.id.btnGame)

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
