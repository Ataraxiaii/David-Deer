package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.LevelOneActivity
import com.daviddeer.daviddeer.LevelTwoActivity

// Game screen
class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val backButton = findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
            finish()  // Return to the previous screen (MainActivity)
        }

        // Navigate to Level One
        val levelOneButton = findViewById<ImageButton>(R.id.btn_level_one)
        levelOneButton.setOnClickListener {
            val intent = Intent(this, LevelOneActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Level Two
        val levelTwoButton = findViewById<ImageButton>(R.id.btn_level_two)
        levelTwoButton.setOnClickListener {
            val intent = Intent(this, LevelTwoActivity::class.java)
            startActivity(intent)
        }
    }
}