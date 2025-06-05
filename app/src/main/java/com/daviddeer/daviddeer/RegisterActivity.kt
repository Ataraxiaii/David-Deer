package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.util.LoginManager

class RegisterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()

            if (username.isNotEmpty() && password.length >= 4) {
                val success = LoginManager.register(this, username, password)
                if (success) {
                    Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "账号已存在", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "用户名不能为空，密码至少4位", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
