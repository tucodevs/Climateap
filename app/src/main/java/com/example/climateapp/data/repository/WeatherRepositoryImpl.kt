package com.example.climateapp.data.repository

import com.example.climateapp.data.*
import com.example.climateapp.data.di.module.WeatherInfo
import com.example.climateapp.data.remote.RemoteDataSource
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : WeatherRepository {

    override suspend fun getWeatherData(lat: Float, lon: Float): WeatherInfo {
        val currentResponse = remoteDataSource.getWeatherDataResponse(lat, lon)
        val hourlyList = remoteDataSource.getHourlyForecast(lat, lon)
        val dailyList = remoteDataSource.getDailyForecast(lat, lon)

        val current = currentResponse.data.firstOrNull()

        return WeatherInfo(
            locationName = current?.cityName ?: "Desconhecido",
            Icon = current?.weather?.icon ?: "01d",
            codeIcon = current?.weather?.code ?: 0,
            condition = current?.weather?.description ?: "Sem descrição",
            temperature = current?.temp ?: 0.0,
            apparentTemperature = current?.appTemp ?: 0.0,
            humidity = current?.rh ?: 0,
            windSpeed = current?.windSpd ?: 0.0,
            rain = current?.precip ?: 0.0,
            hourly = hourlyList,
            daily = dailyList
        )
    }
}
