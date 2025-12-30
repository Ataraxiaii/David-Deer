package com.daviddeer.daviddeer.data

import com.daviddeer.daviddeer.R

object BeastInitialData {

    fun defaultBeasts(): List<Beast> = listOf(
        Beast(1,"Zhu Que",R.drawable.zhuque,
            "ZhuQue, an auspicious crimson phoenix in Chinese mythology, has evolved into a pure and mystical symbol.",
            true,true),

        Beast(2,"Qing Long",R.drawable.qinglong,
            "QingLong, one of ancient China's most potent divine beasts, struck terror into the hearts of demons and wielded boundless power.",
            true,false),

        Beast(3, "Jiu Wei Hu", R.drawable.jiuweihu,
            "Jiu WeiHu, a mysterious and versatile creature of auspicious omen, possesses profound intelligence and the ability to assume human form.",
            true, false),

        Beast(4, "Bai Ze", R.drawable.baize,
            "BaiZe, an auspicious beast in Chinese mythology, could speak human language and ward off all evil forces from the mortal realm.",
            true, false),

        Beast(5, "Fei Yi", R.drawable.feiyi,
            "FeiYi, a monstrous serpent dwelling at the foothills of Mount Hunxi, possessed a single head with a bifurcated body. Its appearance heralded devastating droughts.",
            true, false),

        Beast(6, "David-Deer", R.drawable.daviddeer,
            "David Deer(Si Buxiang), an auspicious omen, has a fox-like head, rabbit ears, a squirrel tail, and deer hind legs.",
            false, false),

        Beast(7, "Jing Wei", R.drawable.jingwei,
            "Jingwei, revered for her tragic heroism, embodies the unyielding spirit that dared to challenge the vast ocean—a testament to perseverance.",
            false, false),

        Beast(8, "Pi Xiu", R.drawable.pixiu,
            "Pixiu, one of China's Five Great Auspicious Beasts, symbolizes the attraction of wealth and guardianship over treasures.",
            false, false),

        Beast(9, "Pu Lao", R.drawable.pulao,
            "Pulao, the fourth son of the Dragon's Nine Offspring, is famed for his thunderous voice. He symbolizes good fortune, career advancement, and renown.",
            false, false),

        Beast(10, "Qi Lin", R.drawable.qilin,
            "Qilin, one of the Five Great Auspicious Beasts, embodies the blessings of seasonal harmony, national peace, and prosperous people.",
            false, false),

        Beast(11, "White Tiger", R.drawable.whitetiger,
            "White Tiger, the Deity of the West among the Four Celestial Guardians, was later venerated in Taoism.",
            false, false),

        Beast(12, "Xuan Wu", R.drawable.xvanwu,
            "Xuanwu, the North Deity among the Four Celestial Guardians, symbolizes longevity.",
            false, false),

        Beast(13, "Ying Long", R.drawable.yinglong,
            "Yinglong, the primordial Chinese deity of Thunder and Rain, governed the four seasons and presided over the mountains and rivers.",
            false, false),
    )
}
