package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
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
            finish() // 返回上一个界面
        }

        // 自动填充用户名
        val savedUsername = LoginManager.getSavedUsername(this)
        if (!savedUsername.isNullOrEmpty()) {
            findViewById<EditText>(R.id.etUsername).setText(savedUsername)
        }

        // 登录按钮
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

        // 注册连接
        findViewById<TextView>(R.id.tvGoRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // 忘记密码连接
        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}
