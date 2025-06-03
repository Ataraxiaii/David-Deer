package com.daviddeer.daviddeer

//静态Beast列表，用于演示
object BeastRepository {

    val beastList = listOf(
        Beast(
            id = 1,
            name = "Zhu Que",
            imageResId = R.drawable.zhuque,
            story = "朱雀，红色的鸟，象征祥瑞的古典的朱雀成了一种纯粹而神秘的符号。"
        ),
        Beast(
            id = 2,
            name = "Qing Long",
            imageResId = R.drawable.qinglong,
            story = "青龙是中国古代最令妖邪胆战且法力无边的神兽之一."
        ),
        Beast(
            id = 3,
            name = "Jiu Wei Hu",
            imageResId = R.drawable.jiuweihu,
            story = "九尾狐通灵性，多姿而神秘，是祥瑞之兽，也可化身为人。"
        )
    )
}
