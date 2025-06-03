package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.R


// 第二关暂时用来演示的成果通关解锁图鉴
class LevelTwoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_two)

        val passButton = findViewById<Button>(R.id.btn_pass_level2)
        passButton.setOnClickListener {
            onLevelTwoPassed()
        }
    }

    private fun onLevelTwoPassed() {
        // 解锁10~13 的灵兽
        BeastRepository.unlockBeastsByIds(listOf(10, 11, 12, 13))

        // 提示通关
        Toast.makeText(this, "Successfully passed the level 2!\n4  new beasts have been unlocked.", Toast.LENGTH_SHORT).show()

        // 返回GameActivity
        val intent = Intent(this, GameActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 清除返回栈
        startActivity(intent)
        finish()
    }
}
