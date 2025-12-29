package com.daviddeer.daviddeer.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StepDbHelper(context: Context) : SQLiteOpenHelper(context, "StepGame.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE step_history (date_str TEXT PRIMARY KEY, count INTEGER)")
        createGoalPresetsTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // version + 1
        if (oldVersion < 2) {
            createGoalPresetsTable(db)
        }
    }

    private fun createGoalPresetsTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS goal_presets (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, target_steps INTEGER)")
        db.execSQL("INSERT INTO goal_presets (name, target_steps) VALUES ('Default', 1000)")
    }

    fun addSteps(date: String, delta: Int) {
        val current = getSteps(date)
        val db = writableDatabase
        val values = ContentValues().apply {
            put("date_str", date)
            put("count", current + delta)
        }
        db.insertWithOnConflict("step_history", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getSteps(date: String): Int {
        val db = readableDatabase
        val cursor = db.query("step_history", arrayOf("count"), "date_str=?", arrayOf(date), null, null, null)
        var steps = 0
        if (cursor.moveToFirst()) {
            steps = cursor.getInt(0)
        }
        cursor.close()
        return steps
    }

    fun getRecentHistory(limit: Int = 7): List<Pair<String, Int>> {
        val list = mutableListOf<Pair<String, Int>>()
        val db = readableDatabase
        val cursor = db.query("step_history", null, null, null, null, null, "date_str DESC", limit.toString())
        while (cursor.moveToNext()) {
            val date = cursor.getString(0)
            val count = cursor.getInt(1)
            list.add(Pair(date, count))
        }
        cursor.close()
        return list
    }

    fun addPreset(name: String, steps: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("target_steps", steps)
        }
        db.insert("goal_presets", null, values)
    }

    fun getAllPresets(): List<Pair<String, Int>> {
        val list = mutableListOf<Pair<String, Int>>()
        val cursor = readableDatabase.rawQuery("SELECT name, target_steps FROM goal_presets", null)
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0) to cursor.getInt(1))
        }
        cursor.close()
        return list
    }

    fun deletePreset(name: String) {
        val db = writableDatabase
        db.delete("goal_presets", "name = ?", arrayOf(name))
        db.close()
    }

    fun updatePreset(name: String, newSteps: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("target_steps", newSteps)
        }
        db.update("goal_presets", values, "name = ?", arrayOf(name))
    }
}