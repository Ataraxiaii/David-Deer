package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.LevelOneActivity
import com.daviddeer.daviddeer.LevelTwoActivity

//游戏界面
class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val backButton = findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
            finish()  // 返回上一界面（MainActivity）
        }

        // 跳转到第一关
        val levelOneButton = findViewById<Button>(R.id.btn_level_one)
        levelOneButton.setOnClickListener {
            val intent = Intent(this, LevelOneActivity::class.java)
            startActivity(intent)
        }

        // 跳转到第二关
        val levelTwoButton = findViewById<Button>(R.id.btn_level_two)
        levelTwoButton.setOnClickListener {
            val intent = Intent(this, LevelTwoActivity::class.java)
            startActivity(intent)
        }
    }
}