package com.project.weathersnap.util

/**
 * Maps WMO Weather interpretation codes to human-readable condition strings and emoji icons.
 * Reference: https://open-meteo.com/en/docs#weathervariables
 */
object WeatherCodeMapper {

    data class WeatherCondition(val description: String, val icon: String)

    fun fromCode(code: Int): WeatherCondition {
        return when (code) {
            0 -> WeatherCondition("Clear Sky", "☀️")
            1 -> WeatherCondition("Mainly Clear", "🌤️")
            2 -> WeatherCondition("Partly Cloudy", "⛅")
            3 -> WeatherCondition("Overcast", "☁️")
            45, 48 -> WeatherCondition("Foggy", "🌫️")
            51 -> WeatherCondition("Light Drizzle", "🌦️")
            53 -> WeatherCondition("Moderate Drizzle", "🌦️")
            55 -> WeatherCondition("Dense Drizzle", "🌧️")
            56, 57 -> WeatherCondition("Freezing Drizzle", "🌧️")
            61 -> WeatherCondition("Slight Rain", "🌦️")
            63 -> WeatherCondition("Moderate Rain", "🌧️")
            65 -> WeatherCondition("Heavy Rain", "🌧️")
            66, 67 -> WeatherCondition("Freezing Rain", "🌧️")
            71 -> WeatherCondition("Slight Snowfall", "🌨️")
            73 -> WeatherCondition("Moderate Snowfall", "🌨️")
            75 -> WeatherCondition("Heavy Snowfall", "❄️")
            77 -> WeatherCondition("Snow Grains", "❄️")
            80 -> WeatherCondition("Slight Showers", "🌦️")
            81 -> WeatherCondition("Moderate Showers", "🌧️")
            82 -> WeatherCondition("Violent Showers", "⛈️")
            85, 86 -> WeatherCondition("Snow Showers", "🌨️")
            95 -> WeatherCondition("Thunderstorm", "⛈️")
            96, 99 -> WeatherCondition("Thunderstorm with Hail", "⛈️")
            else -> WeatherCondition("Unknown", "🌡️")
        }
    }
}
