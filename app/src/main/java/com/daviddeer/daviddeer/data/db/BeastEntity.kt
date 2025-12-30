package com.daviddeer.daviddeer.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beasts")
data class BeastEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val story: String,
    val imageRes: Int,
    val isUnlocked: Boolean,
    val isCaptured: Boolean
)