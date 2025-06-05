package com.daviddeer.daviddeer.data

import android.content.Context
import com.daviddeer.daviddeer.R

/*
The first five beasts are unlocked by default and can be captured directly via map exploration
(The first one is temporarily set as already captured for demonstration purposes).
The remaining eight beasts are locked and must be unlocked by clearing levels in the game
before they can be captured through map exploration.
 */
// Make the list mutable, no longer a static list
object BeastRepository {
    // Initial default list
    private val beastList: MutableList<Beast> = mutableListOf(
        Beast(
            id = 1,
            name = "Zhu Que",
            imageResId = R.drawable.zhuque,
            story = "ZhuQue, an auspicious crimson phoenix in Chinese mythology, has evolved into a pure and mystical symbol.",
            isUnlocked = true,
            isCaptured = true
        ),
        Beast(
            id = 2,
            name = "Qing Long",
            imageResId = R.drawable.qinglong,
            story = "QingLong, one of ancient China's most potent divine beasts, struck terror into the hearts of demons and wielded boundless power.",
            isUnlocked = true,
            isCaptured = false
        ),
        Beast(
            id = 3,
            name = "Jiu Wei Hu",
            imageResId = R.drawable.jiuweihu,
            story = "Jiu WeiHu, a mysterious and versatile creature of auspicious omen, possesses profound intelligence and the ability to assume human form.",
            isUnlocked = true,
            isCaptured = false
        ),
        Beast(
            id = 4,
            name = "Bai Ze",
            imageResId = R.drawable.baize,
            story = "BaiZe, an auspicious beast in Chinese mythology, could speak human language and ward off all evil forces from the mortal realm.",
            isUnlocked = true,
            isCaptured = false
        ),
        Beast(
            id = 5,
            name = "Fei Yi",
            imageResId = R.drawable.feiyi,
            story = "FeiYi, a monstrous serpent dwelling at the foothills of Mount Hunxi, possessed a single head with a bifurcated body. Its appearance heralded devastating droughts.",
            isUnlocked = true,
            isCaptured = false
        ),
        Beast(
            id = 6,
            name = "David-Deer",
            imageResId = R.drawable.daviddeer,
            story = "David Deer(Si Buxiang), an auspicious omen, has a fox-like head, rabbit ears, a squirrel tail, and deer hind legs.",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 7,
            name = "Jing Wei",
            imageResId = R.drawable.jingwei,
            story = "Jingwei, revered for her tragic heroism, embodies the unyielding spirit that dared to challenge the vast ocean—a testament to perseverance.",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 8,
            name = "Pi Xiu",
            imageResId = R.drawable.pixiu,
            story = "Pixiu, one of China's Five Great Auspicious Beasts, symbolizes the attraction of wealth and guardianship over treasures.",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 9,
            name = "Pu Lao",
            imageResId = R.drawable.pulao,
            story = "Pulao, the fourth son of the Dragon's Nine Offspring, is famed for his thunderous voice. He symbolizes good fortune, career advancement, and renown.",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 10,
            name = "Qi Lin",
            imageResId = R.drawable.qilin,
            story = "Qilin, one of the Five Great Auspicious Beasts, embodies the blessings of seasonal harmony, national peace, and prosperous people.",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 11,
            name = "White Tiger",
            imageResId = R.drawable.whitetiger,
            story = "White Tiger, the Deity of the West among the Four Celestial Guardians, was later venerated in Taoism.",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 12,
            name = "Xvan Wu",
            imageResId = R.drawable.xvanwu,
            story = "Xuanwu, the North Deity among the Four Celestial Guardians, symbolizes longevity.",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 13,
            name = "Ying Long",
            imageResId = R.drawable.yinglong,
            story = "Yinglong, the primordial Chinese deity of Thunder and Rain, governed the four seasons and presided over the mountains and rivers.",
            isUnlocked = false,
            isCaptured = false
        )
    )

    // Get data from the initial list
    fun getBeasts(): List<Beast> = beastList

    // Get a beast from the list by its ID
    fun getBeastById(id: Int): Beast? {
        return beastList.find { it.id == id }
    }

    // Unlock specified beasts by ID (via game levels)
    fun unlockBeastsByIds(ids: List<Int>) {
        ids.forEach { id ->
            beastList.find { it.id == id }?.isUnlocked = true
        }
    }

    // 捕捉指定 ID 的灵兽（地图探索）
    fun captureBeast(id: Int) {
        beastList.find { it.id == id }?.isCaptured = true
    }

    // 保存已解锁的 ID
    fun saveUnlockedState(context: Context) {
        val unlockedIds = beastList.filter { it.isUnlocked }.map { it.id }.toSet()
        val prefs = context.getSharedPreferences("beast_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putStringSet("unlocked_ids", unlockedIds.map { it.toString() }.toSet())
            .apply()
    }

    // 从 SharedPreferences 恢复解锁状态
    fun loadUnlockedState(context: Context) {
        val prefs = context.getSharedPreferences("beast_prefs", Context.MODE_PRIVATE)
        val unlockedIds = prefs.getStringSet("unlocked_ids", emptySet())
            ?.mapNotNull { it.toIntOrNull() } ?: emptyList()
        unlockBeastsByIds(unlockedIds)
    }

    // 同时保存解锁和捕捉状态
    fun saveAllStates(context: Context) {
        val prefs = context.getSharedPreferences("beast_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            // 存储解锁状态
            putStringSet("unlocked_ids",
                beastList.filter { it.isUnlocked }.map { it.id.toString() }.toSet())
            // 新增存储捕捉状态
            putStringSet("captured_ids",
                beastList.filter { it.isCaptured }.map { it.id.toString() }.toSet())
        }.apply()
    }

    // 同时加载解锁和捕捉状态
    fun loadAllStates(context: Context) {
        val prefs = context.getSharedPreferences("beast_prefs", Context.MODE_PRIVATE)
        // 加载解锁状态
        prefs.getStringSet("unlocked_ids", emptySet())?.mapNotNull { it.toIntOrNull() }?.let {
            unlockBeastsByIds(it)
        }
        // 新增加载捕捉状态
        prefs.getStringSet("captured_ids", emptySet())?.mapNotNull { it.toIntOrNull() }?.let {
            it.forEach { id -> captureBeast(id) }
        }
    }

}