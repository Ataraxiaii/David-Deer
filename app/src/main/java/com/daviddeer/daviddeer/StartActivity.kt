package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import  com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.util.MusicPlayer
import com.daviddeer.daviddeer.util.LoginManager

// Start screen
class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load unlocked states
        BeastRepository.loadUnlockedState(this)

        setContentView(R.layout.activity_start)

        val startButton = findViewById<ImageButton>(R.id.startButton)
        startButton.setOnClickListener {
            if (!LoginManager.isLoggedIn(this)) {
                // Launch LoginActivity without calling finish(), keeping StartActivity in the background
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Only destroy StartActivity when jumping to MainActivity
            }
        }
    }
}
