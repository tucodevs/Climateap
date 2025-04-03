package com.example.climateapp.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class HourlyForecastResponse(
    val data: List<HourlyForecastDto>
)

@Serializable
data class HourlyForecastDto(
    val timestamp_local: String,
    val temp: Double,
    val weather: WeatherIconDto
)

@Serializable
data class WeatherIconDto(
    val icon: String
)
