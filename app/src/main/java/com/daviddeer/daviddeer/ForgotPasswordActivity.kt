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

        // 返回按钮
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish() // 返回上一个界面
        }

        // 重置密码按钮
        findViewById<Button>(R.id.btnResetPassword).setOnClickListener {
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val newPassword = findViewById<EditText>(R.id.etNewPassword).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword).text.toString()

            if (username.isEmpty()) {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "请输入并确认新密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.length < 4) {
                Toast.makeText(this, "密码长度至少4位", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 调用LoginManager重置密码
            val success = LoginManager.resetPassword(this, username, newPassword)

            if (success) {
                Toast.makeText(this, "密码重置成功，请使用新密码登录", Toast.LENGTH_SHORT).show()
                finish() // 关闭当前界面，返回登录界面
            } else {
                Toast.makeText(this, "用户名不存在", Toast.LENGTH_SHORT).show()
            }
        }
    }
}