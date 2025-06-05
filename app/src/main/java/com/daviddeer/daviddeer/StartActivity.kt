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
            if (!LoginManager.isLoggedIn(this)) {
                // 启动LoginActivity但不调用finish()，保持StartActivity在后台
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish() // 只有跳转到MainActivity时才销毁StartActivity
            }
        }
    }
}

