package com.daviddeer.daviddeer.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BeastDao {

    @Query("SELECT * FROM beasts")
    fun getAll(): List<BeastEntity>

    // search a beast by name
    @Query("SELECT * FROM beasts WHERE name LIKE '%' || :keyword || '%'")
    fun searchByName(keyword: String): List<BeastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(beasts: List<BeastEntity>)
}
