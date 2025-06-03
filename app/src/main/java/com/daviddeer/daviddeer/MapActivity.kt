package com.daviddeer.daviddeer

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity

class MapActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val backButton = findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
<<<<<<< HEAD
            finish()  // 返回上一界面（MainActivity
             
=======
            finish()  // 返回上一界面（MainActivity）
>>>>>>> fba2954d96ae638b4df89b4d8a48035f0871a533
        }
    }
}