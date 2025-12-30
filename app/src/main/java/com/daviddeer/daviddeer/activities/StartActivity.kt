package com.daviddeer.daviddeer.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.data.Beast
import com.daviddeer.daviddeer.data.BeastRepository
import com.daviddeer.daviddeer.data.db.BeastDatabase
import com.daviddeer.daviddeer.data.db.BeastEntity
import com.daviddeer.daviddeer.util.LoginManager

// Start screen
class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // write beast data to database
        Thread {
            val dao = BeastDatabase.getInstance(this).beastDao()

            dao.insertAll(
                listOf(
                    BeastEntity(1, "Zhu Que", "ZhuQue, an auspicious crimson phoenix in Chinese mythology, has evolved into a pure and mystical symbol.", R.drawable.zhuque, true, true),
                    BeastEntity(2,"Qing Long", "QingLong, one of ancient China's most potent divine beasts, struck terror into the hearts of demons and wielded boundless power.", R.drawable.qinglong, true, false),
                    BeastEntity(3, "Jiu Wei Hu", "Jiu WeiHu, a mysterious and versatile creature of auspicious omen, possesses profound intelligence and the ability to assume human form.", R.drawable.jiuweihu, true, false),
                    BeastEntity(4, "Bai Ze", "BaiZe, an auspicious beast in Chinese mythology, could speak human language and ward off all evil forces from the mortal realm.", R.drawable.baize, true, false),
                    BeastEntity(5, "Fei Yi", "FeiYi, a monstrous serpent dwelling at the foothills of Mount Hunxi, possessed a single head with a bifurcated body. Its appearance heralded devastating droughts.", R.drawable.feiyi, true, false),
                    BeastEntity(6, "David-Deer", "David Deer(Si Buxiang), an auspicious omen, has a fox-like head, rabbit ears, a squirrel tail, and deer hind legs.", R.drawable.daviddeer, false, false),
                    BeastEntity(7, "Jing Wei", "Jingwei, revered for her tragic heroism, embodies the unyielding spirit that dared to challenge the vast ocean—a testament to perseverance.", R.drawable.jingwei, false, false),
                    BeastEntity(8, "Pi Xiu", "Pixiu, one of China's Five Great Auspicious Beasts, symbolizes the attraction of wealth and guardianship over treasures.", R.drawable.pixiu, false, false),
                    BeastEntity(9, "Pu Lao", "Pulao, the fourth son of the Dragon's Nine Offspring, is famed for his thunderous voice. He symbolizes good fortune, career advancement, and renown.", R.drawable.pulao, false, false),
                    BeastEntity(10, "Qi Lin", "Qilin, one of the Five Great Auspicious Beasts, embodies the blessings of seasonal harmony, national peace, and prosperous people.", R.drawable.qilin, false, false),
                    BeastEntity(11, "White Tiger", "White Tiger, the Deity of the West among the Four Celestial Guardians, was later venerated in Taoism.", R.drawable.whitetiger, false, false),
                    BeastEntity(12, "Xuan Wu", "Xuanwu, the North Deity among the Four Celestial Guardians, symbolizes longevity.", R.drawable.xvanwu, false, false),
                    BeastEntity(13, "Ying Long", "Yinglong, the primordial Chinese deity of Thunder and Rain, governed the four seasons and presided over the mountains and rivers.", R.drawable.yinglong, false, false)
                )
            )
        }.start()


        // Load unlocked states
        BeastRepository.loadUnlockedState(this)

        setContentView(R.layout.activity_start)

        val startButton = findViewById<ImageButton>(R.id.startButton)
        startButton.setOnClickListener {
            if (!LoginManager.isLoggedIn(this)) {
                // Launch LoginActivity without calling finish(), keeping StartActivity in the background
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Only destroy StartActivity when jumping to MainActivity
            }
        }
    }
}
