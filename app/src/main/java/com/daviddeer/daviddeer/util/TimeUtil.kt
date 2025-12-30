package com.daviddeer.daviddeer.util

import android.icu.util.Calendar

object TimeUtil {
    fun isNight(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour < 6 || hour >= 18
    }
}