package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.R


// 第一关暂时用来演示的成果通关解锁图鉴
class LevelOneActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_one)

        val passButton = findViewById<Button>(R.id.btn_pass_level1)
        passButton.setOnClickListener {
            onLevelOnePassed()
        }
    }

    private fun onLevelOnePassed() {
        // 解锁 6～9 的灵兽
        BeastRepository.unlockBeastsByIds(listOf(6, 7, 8, 9))
        // 每次通关后保存解锁状态
        BeastRepository.saveUnlockedState(this)

        // 提示通关
        Toast.makeText(this, "Successfully passed the level 1!\n4  new beasts have been unlocked.", Toast.LENGTH_SHORT).show()

        // 返回GameActivity
        val intent = Intent(this, GameActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 清除返回栈
        startActivity(intent)
        finish()
    }
}
