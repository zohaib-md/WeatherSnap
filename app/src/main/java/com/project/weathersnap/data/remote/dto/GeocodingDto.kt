package com.project.weathersnap.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    val results: List<CityDto>?
)

data class CityDto(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val admin1: String? = null, // State / region
    @SerializedName("country_code") val countryCode: String? = null
)
