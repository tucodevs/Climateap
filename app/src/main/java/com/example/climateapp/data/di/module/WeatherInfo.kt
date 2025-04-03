package com.example.climateapp.data.di.module

import com.example.climateapp.data.DailyForecast
import com.example.climateapp.data.HourlyForecast

data class WeatherInfo(
    val locationName: String? = null,
    val Icon: String? = null,
    val codeIcon: Int? = null, // você pode remover se não estiver usando
    val condition: String? = null,
    val temperature: Double? = null,
    val apparentTemperature: Double? = null,
    val humidity: Int? = null,
    val windSpeed: Double? = null,
    val rain: Double? = null,
    val hourly: List<HourlyForecast> = emptyList(),
    val daily: List<DailyForecast> = emptyList()
)
