package com.daviddeer.daviddeer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BeastEntity::class], version = 1)
abstract class BeastDatabase : RoomDatabase() {

    abstract fun beastDao(): BeastDao

    companion object {
        @Volatile private var INSTANCE: BeastDatabase? = null

        fun getInstance(context: Context): BeastDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BeastDatabase::class.java,
                    "beast_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
