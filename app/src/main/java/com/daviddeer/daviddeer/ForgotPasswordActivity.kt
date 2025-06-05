package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.util.LoginManager

class ForgotPasswordActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish() // Return to the previous screen
        }

        // Reset password button
        findViewById<Button>(R.id.btnResetPassword).setOnClickListener {
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val newPassword = findViewById<EditText>(R.id.etNewPassword).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword).text.toString()

            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter your username.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please enter and confirm the new password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "The two entered passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.length < 4) {
                Toast.makeText(this, "The password length must be at least 4 characters.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call LoginManager to reset the password
            val success = LoginManager.resetPassword(this, username, newPassword)

            if (success) {
                Toast.makeText(this, "Password reset successful, please use the new password to log in.", Toast.LENGTH_SHORT).show()
                finish() // Close the current screen and return to the login screen
            } else {
                Toast.makeText(this, "The username does not exist.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}