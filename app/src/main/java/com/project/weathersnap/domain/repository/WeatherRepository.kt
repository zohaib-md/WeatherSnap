package com.project.weathersnap.domain.repository

import com.project.weathersnap.data.local.ReportEntity
import com.project.weathersnap.data.remote.dto.CityDto
import com.project.weathersnap.data.remote.dto.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    // Remote API calls
    suspend fun searchCity(query: String): Result<List<CityDto>>
    suspend fun getWeather(lat: Double, lon: Double): Result<WeatherResponse>

    // Local DB calls
    suspend fun saveReport(report: ReportEntity)
    fun getAllSavedReports(): Flow<List<ReportEntity>>
}