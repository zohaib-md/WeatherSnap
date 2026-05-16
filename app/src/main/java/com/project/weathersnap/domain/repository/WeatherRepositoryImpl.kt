package com.project.weathersnap.domain.repository

import com.project.weathersnap.data.local.ReportDao
import com.project.weathersnap.data.local.ReportEntity
import com.project.weathersnap.data.remote.GeocodingApiService
import com.project.weathersnap.data.remote.WeatherApiService
import com.project.weathersnap.data.remote.dto.CityDto
import com.project.weathersnap.data.remote.dto.WeatherResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val geocodingApiService: GeocodingApiService,
    private val reportDao: ReportDao
) : WeatherRepository {

    // Simple in-memory cache to satisfy the assignment requirement
    private val citySearchCache = mutableMapOf<String, List<CityDto>>()

    override suspend fun searchCity(query: String): Result<List<CityDto>> {
        // Return cached result if we already searched this exact query
        if (citySearchCache.containsKey(query)) {
            return Result.success(citySearchCache[query]!!)
        }

        return try {
            val response = geocodingApiService.searchCity(query)
            val results = response.results ?: emptyList()
            // Save to cache before returning
            citySearchCache[query] = results
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWeather(lat: Double, lon: Double): Result<WeatherResponse> {
        return try {
            val response = weatherApiService.getWeather(lat, lon)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveReport(report: ReportEntity) {
        reportDao.insertReport(report)
    }

    override fun getAllSavedReports(): Flow<List<ReportEntity>> {
        return reportDao.getAllReports()
    }
}