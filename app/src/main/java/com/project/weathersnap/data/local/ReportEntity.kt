package com.project.weathersnap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // Weather snapshot data
    val cityName: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double,
    // Report specific data
    val imagePath: String, // Local URI path to the compressed image
    val notes: String,
    val originalImageSizeBytes: Long,
    val compressedImageSizeBytes: Long,
    val timestamp: Long = System.currentTimeMillis()
)