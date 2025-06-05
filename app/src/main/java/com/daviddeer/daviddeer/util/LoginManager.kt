package com.daviddeer.daviddeer.util

import android.content.Context

object LoginManager {
    private const val PREF_NAME = "user_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_LOGGED_IN = "is_logged_in"
    private const val KEY_SAVED_USERNAME = "saved_username" // 保存的用户名

    fun register(context: Context, username: String, password: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (prefs.contains(KEY_USERNAME)) return false // 已注册

        prefs.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            putBoolean(KEY_LOGGED_IN, true)
            putString(KEY_SAVED_USERNAME, username) // 保存用户名
            apply()
        }
        return true
    }

    fun login(context: Context, username: String, password: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val savedUser = prefs.getString(KEY_USERNAME, null)
        val savedPass = prefs.getString(KEY_PASSWORD, null)

        // 验证用户名和密码
        if (username == savedUser && password == savedPass) {
            prefs.edit().apply {
                putBoolean(KEY_LOGGED_IN, true)
                putString(KEY_SAVED_USERNAME, username) // 保存用户名
                apply()
            }
            return true
        }

        return false
    }

    fun resetPassword(context: Context, username: String, newPassword: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val savedUser = prefs.getString(KEY_USERNAME, null)

        // 检查用户名是否匹配（单用户模式）
        if (savedUser != username) {
            return false
        }
        // 更新密码
        prefs.edit().putString(KEY_PASSWORD, newPassword).apply()
        return true
    }

    fun getSavedUsername(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SAVED_USERNAME, null)
    }


    fun isLoggedIn(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_LOGGED_IN, false)
    }

    fun logout(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_LOGGED_IN, false).apply()
    }
}
