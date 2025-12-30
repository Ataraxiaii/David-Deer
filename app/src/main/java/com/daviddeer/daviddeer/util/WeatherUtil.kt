package com.daviddeer.daviddeer.util

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object WeatherUtil {

    private const val API_KEY = "010ff02ec6ff0fa6115d3fa37a9d2ecc"

    fun getWeather(
        lat: Double,
        lon: Double,
        callback: (String) -> Unit
    ) {
        val url =
            "https://api.openweathermap.org/data/2.5/weather" +
                    "?lat=$lat&lon=$lon&appid=$API_KEY"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        Thread {
            try {
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@Thread

                val json = JSONObject(body)
                val weatherMain =
                    json.getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("main")

                val result = when (weatherMain) {
                    "Rain", "Drizzle" -> "Rain"
                    "Snow" -> "Snow"
                    "Fog", "Mist", "Haze" -> "Fog"
                    "Wind", "Squall" -> "Wind"
                    else -> "Clear"
                }

                callback(result)

            } catch (e: Exception) {
                callback("Clear")
            }
        }.start()
    }
}
