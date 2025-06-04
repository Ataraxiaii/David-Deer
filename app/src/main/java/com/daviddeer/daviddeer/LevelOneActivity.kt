package com.daviddeer.daviddeer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.R


// 第一关 翻卡牌记忆游戏
class LevelOneActivity : ComponentActivity() {

    private lateinit var cards: List<ImageButton>
    private lateinit var tvRules: TextView

    private lateinit var cardImages: List<Int>
    private var firstSelected: ImageButton? = null
    private var pairsFound = 0
    private val totalPairs = 4  // 总共4对卡牌

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_one)

        // 返回按钮
        val backButton = findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
            finish()  // 返回上一界面（GameActivity）
        }

        tvRules = findViewById(R.id.tvRules)
        tvRules.text = "Find all the paired beast cards!"

        // 初始化卡片资源（4对图片）
        cardImages = listOf(
            R.drawable.daviddeer, R.drawable.pixiu, R.drawable.pixiu, R.drawable.jingwei,
            R.drawable.daviddeer, R.drawable.jingwei, R.drawable.pulao, R.drawable.pulao
        ).shuffled()

        // 获取 8 个卡片按钮
        cards = listOf(
            findViewById(R.id.card1),
            findViewById(R.id.card2),
            findViewById(R.id.card3),
            findViewById(R.id.card4),
            findViewById(R.id.card5),
            findViewById(R.id.card6),
            findViewById(R.id.card7),
            findViewById(R.id.card8)
        )

        cards.forEachIndexed { index, card ->
            card.setImageResource(R.drawable.beastlocked)  // 卡背图, 用未解锁灵兽图代替
            card.tag = cardImages[index]  // 暂存对应的图片资源ID

            card.setOnClickListener {
                flipCard(card, index)
            }
        }
    }

    // 翻卡牌
    private fun flipCard(card: ImageButton, index: Int) {
        val imageRes = cardImages[index]

        // 若已经翻开或已匹配，不允许再次翻动
        if (card.drawable.constantState == resources.getDrawable(imageRes, null).constantState) return

        card.setImageResource(imageRes)

        if (firstSelected == null) {
            firstSelected = card
        } else {
            // 第二次翻牌，检查是否匹配
            val firstIndex = cards.indexOf(firstSelected!!)
            val firstImage = cardImages[firstIndex]

            if (firstImage == imageRes && firstSelected != card) {
                // 成功匹配
                firstSelected?.isEnabled = false
                card.isEnabled = false
                pairsFound++
                firstSelected = null

                if (pairsFound == totalPairs) {
                    onLevelOnePassed()
                }
            } else {
                // 匹配失败，延迟翻回
                Handler(Looper.getMainLooper()).postDelayed({
                    firstSelected?.setImageResource(R.drawable.beastlocked)
                    card.setImageResource(R.drawable.beastlocked)
                    firstSelected = null
                }, 600)
            }
        }
    }

    private fun onLevelOnePassed() {
        // 提示通关
        Toast.makeText(this, "Successfully passed the level 1!\n4  new beasts have been unlocked.", Toast.LENGTH_SHORT).show()
        // 解锁 6～9 的灵兽
        BeastRepository.unlockBeastsByIds(listOf(6, 7, 8, 9))
        // 每次通关后保存解锁状态
        BeastRepository.saveUnlockedState(this)

        Handler(Looper.getMainLooper()).postDelayed({
            finish() // 返回 GameActivity
        }, 2000)
//        // 返回GameActivity
//        val intent = Intent(this, GameActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 清除返回栈
//        startActivity(intent)
//        finish()
    }
}
