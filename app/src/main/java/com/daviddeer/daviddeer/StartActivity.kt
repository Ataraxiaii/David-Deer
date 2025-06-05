package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import  com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.util.MusicPlayer
import com.daviddeer.daviddeer.util.LoginManager

//开始界面
class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 加载解锁状态
        BeastRepository.loadUnlockedState(this)

        setContentView(R.layout.activity_start)

        val startButton = findViewById<ImageButton>(R.id.startButton)
        startButton.setOnClickListener {
            // 判断是否登录
            if (!LoginManager.isLoggedIn(this)) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            finish() // 不让用户按返回键返回开始界面
        }
    }
}

