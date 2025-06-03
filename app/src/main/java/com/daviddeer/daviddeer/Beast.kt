package com.daviddeer.daviddeer

//灵兽的数据结构
data class Beast(
    val id: Int,                 // 唯一 ID
    val name: String,            // 灵兽名称
    val imageResId: Int,         // 图片资源 ID
    val story: String,           // 灵兽的故事内容
    var isUnlocked: Boolean = false,   // 是否通过游戏解锁（是否显示彩图）
    var isCaptured: Boolean = false    // 是否通过地图捕捉（是否显示名字与故事）
)
