package com.example.climateapp.data.remote

import com.example.climateapp.data.DailyForecast
import com.example.climateapp.data.HourlyForecast
import com.example.climateapp.data.remote.response.WeatherDataResponse

interface RemoteDataSource {
    suspend fun getWeatherDataResponse(lat: Float, lng: Float): WeatherDataResponse
    suspend fun getHourlyForecast(lat: Float, lon: Float): List<HourlyForecast>
    suspend fun getDailyForecast(lat: Float, lon: Float): List<DailyForecast>
}
