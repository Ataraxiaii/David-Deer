package com.daviddeer.daviddeer.util

import com.daviddeer.daviddeer.R

object BackgroundUtil {

    fun getBackground(weather: String, isNight: Boolean): Int {
        return when {
            !isNight && weather == "Rain" -> R.drawable.rain
            !isNight && weather == "Snow" -> R.drawable.snow
            !isNight && weather == "Fog" -> R.drawable.fog
            !isNight && weather == "Wind" -> R.drawable.wind
            !isNight -> R.drawable.homepage

            isNight && weather == "Rain" -> R.drawable.night_rain
            isNight && weather == "Snow" -> R.drawable.night_snow
            isNight && weather == "Fog" -> R.drawable.night_fog
            isNight && weather == "Wind" -> R.drawable.night_wind
            else -> R.drawable.night
        }
    }
}
