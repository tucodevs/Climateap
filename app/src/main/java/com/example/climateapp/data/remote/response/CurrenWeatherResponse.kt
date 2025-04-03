package com.example.climateapp.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherResponse(
    @SerialName("data")
    val data: List<CurrentWeatherData>
)

@Serializable
data class CurrentWeatherData(
    @SerialName("city_name") val cityName: String,
    @SerialName("temp") val temp: Double,
    @SerialName("app_temp") val appTemp: Double,
    @SerialName("rh") val rh: Int,
    @SerialName("wind_spd") val windSpd: Double,
    @SerialName("precip") val precip: Double,
    @SerialName("weather") val weather: WeatherInfo
)
