package com.daviddeer.daviddeer.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.util.LoginManager

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (LoginManager.isLoggedIn(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish() // Return to the previous screen
        }

        // Auto-fill username
        val savedUsername = LoginManager.getSavedUsername(this)
        if (!savedUsername.isNullOrEmpty()) {
            findViewById<EditText>(R.id.etUsername).setText(savedUsername)
        }

        // Login button
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()

            if (LoginManager.login(this, username, password)) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show()
            }
        }

        // Register link
        findViewById<TextView>(R.id.tvGoRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Forgot password link
        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}
