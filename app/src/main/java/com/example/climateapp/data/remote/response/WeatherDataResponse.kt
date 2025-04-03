package com.example.climateapp.data.remote.response

import com.example.climateapp.data.CurrentWeather
import com.example.climateapp.data.DailyForecast
import com.example.climateapp.data.HourlyForecast
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDataResponse(
    val current: CurrentWeather? = null,
    val hourly: List<HourlyForecast> = emptyList(),
    val daily: List<DailyForecast> = emptyList()
)
