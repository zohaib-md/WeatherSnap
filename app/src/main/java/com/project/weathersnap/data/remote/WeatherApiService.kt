package com.project.weathersnap.data.remote

import com.project.weathersnap.data.remote.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        // Requesting exactly the data the assignment requires
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,weather_code,surface_pressure,wind_speed_10m"
    ): WeatherResponse
}