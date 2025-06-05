package com.daviddeer.daviddeer.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.util.LoginManager
import android.widget.TextView
import com.daviddeer.daviddeer.R

class RegisterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish() // Return to the previous screen
        }

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()

            if (username.isNotEmpty() && password.length >= 4) {
                val success = LoginManager.register(this, username, password)
                if (success) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "The account already exists.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Username cannot be empty, and the password must be at least 4 characters long.", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.tvGoLogin).setOnClickListener {
            finish() // Or startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
