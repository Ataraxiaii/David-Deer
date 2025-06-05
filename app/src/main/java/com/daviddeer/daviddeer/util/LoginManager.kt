package com.daviddeer.daviddeer.util

import android.content.Context

object LoginManager {
    private const val PREF_NAME = "user_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_LOGGED_IN = "is_logged_in"

    fun register(context: Context, username: String, password: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (prefs.contains(KEY_USERNAME)) return false // 已注册

        prefs.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            putBoolean(KEY_LOGGED_IN, true)
            apply()
        }
        return true
    }

    fun login(context: Context, username: String, password: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val savedUser = prefs.getString(KEY_USERNAME, null)
        val savedPass = prefs.getString(KEY_PASSWORD, null)

        if (username == savedUser && password == savedPass) {
            prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply()
            return true
        }
        return false
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
