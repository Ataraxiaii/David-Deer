package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()

            if (LoginManager.login(this, username, password)) {
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.tvGoRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
