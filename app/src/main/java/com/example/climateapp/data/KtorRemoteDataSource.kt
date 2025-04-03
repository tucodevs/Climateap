package com.example.climateapp.data

import android.util.Log
import com.example.climateapp.data.remote.RemoteDataSource
import com.example.climateapp.data.remote.response.CurrentWeatherResponse
import com.example.climateapp.data.remote.response.HourlyForecastResponse
import com.example.climateapp.data.remote.response.DailyForecastResponse
import com.example.climateapp.data.remote.response.WeatherDataResponse
import com.example.climateapp.data.HourlyForecast
import com.example.climateapp.data.DailyForecast
import com.example.climateapp.data.CurrentWeather
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class KtorRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient
) : RemoteDataSource {

    companion object {
        private const val BASE_URL = "https://api.weatherbit.io/v2.0"
        private const val API_KEY = "f4e94ecc2f0042f6b727f985999d97ff"
    }

    override suspend fun getWeatherDataResponse(lat: Float, lng: Float): WeatherDataResponse {
        Log.d("ClimaApp", "Chamando endpoint /current")

        val currentResponse: CurrentWeatherResponse = httpClient.get("$BASE_URL/current") {
            url {
                parameters.append("lat", lat.toString())
                parameters.append("lon", lng.toString())
                parameters.append("key", API_KEY)
                parameters.append("lang", "pt")
            }
        }.body()

        val hourlyResponse: HourlyForecastResponse = httpClient.get("$BASE_URL/forecast/hourly") {
            url {
                parameters.append("lat", lat.toString())
                parameters.append("lon", lng.toString())
                parameters.append("key", API_KEY)
                parameters.append("lang", "pt")
                parameters.append("hours", "6")
            }
        }.body()

        val dailyResponse: DailyForecastResponse = httpClient.get("$BASE_URL/forecast/daily") {
            url {
                parameters.append("lat", lat.toString())
                parameters.append("lon", lng.toString())
                parameters.append("key", API_KEY)
                parameters.append("lang", "pt")
                parameters.append("days", "7")
            }
        }.body()

        return WeatherDataResponse(
            current = currentResponse.data.firstOrNull()?.let {
                CurrentWeather(
                    temperature = it.temperature,
                    humidity = it.relativeHumidity,
                    windSpeed = it.windSpeed,
                    rain = it.precipitation,
                    description = it.weather.description,
                    icon = it.weather.icon
                )
            },
            hourly = hourlyResponse.data.map {
                HourlyForecast(
                    time = it.timestampLocal.substringAfter("T"), // tipo "14:00"
                    temperature = it.temp,
                    icon = it.weather.icon
                )
            },
            daily = dailyResponse.data.map {
                DailyForecast(
                    date = it.validDate,
                    maxTemperature = it.maxTemp,
                    minTemperature = it.minTemp,
                    icon = it.weather.icon
                )
            }
        )
    }

    override suspend fun getHourlyForecast(lat: Float, lon: Float): List<HourlyForecast> {
        Log.d("ClimaApp", "Chamando forecast/hourly")

        val response: HourlyForecastResponse = httpClient.get("$BASE_URL/forecast/hourly") {
            url {
                parameters.append("lat", lat.toString())
                parameters.append("lon", lon.toString())
                parameters.append("key", API_KEY)
                parameters.append("lang", "pt")
                parameters.append("hours", "6")
            }
        }.body()

        return response.data.map {
            HourlyForecast(
                time = it.timestampLocal.substringAfter("T"),
                temperature = it.temp,
                icon = it.weather.icon
            )
        }
    }

    override suspend fun getDailyForecast(lat: Float, lon: Float): List<DailyForecast> {
        Log.d("ClimaApp", "Chamando forecast/daily")

        val response: DailyForecastResponse = httpClient.get("$BASE_URL/forecast/daily") {
            url {
                parameters.append("lat", lat.toString())
                parameters.append("lon", lon.toString())
                parameters.append("key", API_KEY)
                parameters.append("lang", "pt")
                parameters.append("days", "7")
            }
        }.body()

        return response.data.map {
            DailyForecast(
                date = it.validDate,
                maxTemperature = it.maxTemp,
                minTemperature = it.minTemp,
                icon = it.weather.icon
            )
        }
    }
}
