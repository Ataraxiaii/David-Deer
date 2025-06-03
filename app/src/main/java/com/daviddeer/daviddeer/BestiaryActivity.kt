package com.daviddeer.daviddeer

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity

//图鉴界面
class BestiaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bestiary)

        val backButton = findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
            finish()  // 返回上一界面（MainActivity）
        }
    }
}
