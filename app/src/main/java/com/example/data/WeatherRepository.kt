package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class WeatherData(
    val temperature: String = "21°C",
    val condition: String = "CLEAR SKY",
    val weatherCode: Int = 0,
    val high: String = "24°C",
    val low: String = "15°C"
)

class WeatherRepository(private val context: Context) {

    private var lastFetchTime = 0L
    private var cachedWeather = WeatherData()

    suspend fun getWeather(lat: Double = 51.5074, lon: Double = -0.1278, city: String = "London"): WeatherData {
        val now = System.currentTimeMillis()
        if (now - lastFetchTime < 30 * 60 * 1000 && lastFetchTime > 0) {
            return cachedWeather
        }

        return withContext(Dispatchers.IO) {
            try {
                val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,weather_code&daily=temperature_2m_max,temperature_2m_min&timezone=auto"
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.requestMethod = "GET"

                if (conn.responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val response = reader.readText()
                    reader.close()

                    val json = JSONObject(response)
                    val current = json.getJSONObject("current")
                    val temp = current.getDouble("temperature_2m").toInt()
                    val code = current.getInt("weather_code")

                    var hi = "${temp + 3}°C"
                    var lo = "${temp - 5}°C"
                    try {
                        val daily = json.getJSONObject("daily")
                        val maxArr = daily.getJSONArray("temperature_2m_max")
                        val minArr = daily.getJSONArray("temperature_2m_min")
                        if (maxArr.length() > 0) hi = "${maxArr.getDouble(0).toInt()}°C"
                        if (minArr.length() > 0) lo = "${minArr.getDouble(0).toInt()}°C"
                    } catch (_: Exception) {}

                    val condition = decodeWmoCode(code)
                    cachedWeather = WeatherData(
                        temperature = "${temp}°C",
                        condition = condition,
                        weatherCode = code,
                        high = hi,
                        low = lo
                    )
                    lastFetchTime = now
                    return@withContext cachedWeather
                }
            } catch (e: Exception) {
                Log.e("WeatherRepository", "Failed to fetch weather from Open-Meteo", e)
            }
            cachedWeather
        }
    }

    private fun decodeWmoCode(code: Int): String {
        return when (code) {
            0 -> "CLEAR SKY"
            1, 2, 3 -> "PARTLY CLOUDY"
            45, 48 -> "FOG"
            51, 53, 55 -> "DRIZZLE"
            61, 63, 65 -> "RAIN"
            71, 73, 75 -> "SNOW"
            80, 81, 82 -> "SHOWERS"
            95, 96, 99 -> "THUNDERSTORM"
            else -> "CLOUDY"
        }
    }
}
