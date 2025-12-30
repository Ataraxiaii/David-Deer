package com.daviddeer.daviddeer.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.data.db.BeastDatabase
import com.daviddeer.daviddeer.data.db.BeastEntity
import com.daviddeer.daviddeer.util.LoginManager

// Start screen
class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // write beast data to database
        Thread {
            val dao = BeastDatabase.getInstance(this).beastDao()

            dao.insertAll(
                listOf(
                    BeastEntity(1, "Qilin", "A sacred beast of prosperity.", R.drawable.qilin, true, false),
                    BeastEntity(2, "White Tiger", "Guardian of the West.", R.drawable.whitetiger, true, true)
                )
            )
        }.start()


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
