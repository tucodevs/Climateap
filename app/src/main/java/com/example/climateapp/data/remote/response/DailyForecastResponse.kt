package com.example.climateapp.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class DailyForecastResponse(
    val data: List<DailyForecastDto>
)

@Serializable
data class DailyForecastDto(
    val valid_date: String,
    val max_temp: Double,
    val min_temp: Double,
    val weather: WeatherIconDto
)
