package com.daviddeer.daviddeer.data

import com.daviddeer.daviddeer.R

/*静态Beast列表
前五个灵兽默认已经解锁，可以直接通过地图探索来捕捉 （这里面第1个暂时先设置为了已经被捕捉到，用来便于演示）
后面是个灵兽都是被锁住的，需要先通过游戏闯关解锁图鉴，再去地图探索进行捕捉
 */
object BeastRepository {

    fun getBeasts():
    List<Beast> = listOf(
        Beast(
            id = 1,
            name = "Zhu Que",
            imageResId = R.drawable.zhuque,
            story = "朱雀，红色的鸟，象征祥瑞的古典的朱雀成了一种纯粹而神秘的符号。",
            isUnlocked = true,
            isCaptured = true
        ),
        Beast(
            id = 2,
            name = "Qing Long",
            imageResId = R.drawable.qinglong,
            story = "青龙是中国古代最令妖邪胆战且法力无边的神兽之一.",
            isUnlocked = true,
            isCaptured = false
        ),
        Beast(
            id = 3,
            name = "Jiu Wei Hu",
            imageResId = R.drawable.jiuweihu,
            story = "九尾狐通灵性，多姿而神秘，是祥瑞之兽，也可化身为人。",
            isUnlocked = true,
            isCaptured = false
        ),
        Beast(
            id = 4,
            name = "Bai Ze",
            imageResId = R.drawable.baize,
            story = "白泽，中国古代神话中的瑞兽。能言语，通万物之情，知鬼神之事，“王者有德”才出现，能辟除人间一切邪气。",
            isUnlocked = true,
            isCaptured = false
        ),
        Beast(
            id = 5,
            name = "Fei Yi",
            imageResId = R.drawable.feiyi,
            story = "肥遗是一种居住在浑夕山山麓的怪蛇，有一个头、两个身体，出现的地方就会有大旱。",
            isUnlocked = true,
            isCaptured = false
        ),
        Beast(
            id = 6,
            name = "David-Deer",
            imageResId = R.drawable.daviddeer,
            story = "David Deer也叫四不相。它的头部和身体像狐狸、耳朵像兔子、尾巴像松鼠、后腿像鹿，是一种吉祥象征。",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 7,
            name = "Jing Wei",
            imageResId = R.drawable.jingwei,
            story = "精卫具有敢于向大海抗争、锲而不舍的悲壮精神，为人们所敬仰。",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 8,
            name = "Pi Xiu",
            imageResId = R.drawable.pixiu,
            story = "貔貅是中国的五大瑞兽之一，具有财富和守护财报的象征。",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 9,
            name = "Pu Lao",
            imageResId = R.drawable.pulao,
            story = "蒲牢“龙生九子”中的第四子，以声音洪亮著称，象征吉祥、高升，寓意声名远扬",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 10,
            name = "Qi Lin",
            imageResId = R.drawable.qilin,
            story = "麒麟五大瑞兽之一，蕴含“风调雨顺、国泰民安”的美好寓意。",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 11,
            name = "White Tiger",
            imageResId = R.drawable.whitetiger,
            story = "白虎四方四神之一，西方之神，后为道教所信奉。",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 12,
            name = "Xvan Wu",
            imageResId = R.drawable.xvanwu,
            story = "玄武四方四神之一，北方之神，象征长寿。",
            isUnlocked = false,
            isCaptured = false
        ),
        Beast(
            id = 13,
            name = "Ying Long",
            imageResId = R.drawable.yinglong,
            story = "应龙是中国最初的雷神、雨神，是掌管四季、山河的神明。",
            isUnlocked = false,
            isCaptured = false
        )
    )
}