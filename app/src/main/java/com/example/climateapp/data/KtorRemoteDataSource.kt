package com.example.climateapp.data

import com.example.climateapp.data.remote.RemoteDataSource
import com.example.climateapp.data.remote.response.WeatherDataResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class KtorRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient
) : RemoteDataSource {



    companion object {
        private const val BASE_URL = "http://api.weatherbit.io/v2.0/current"
        private const val API_KEY = "f4e94ecc2f0042f6b727f985999d97ff"
    }

    override suspend fun getWeatherDataResponse(lat: Float, lng: Float): WeatherDataResponse {

        return httpClient
            .get("${BASE_URL}?lat=$lat&lon=$lng&key=${API_KEY}")
            .body()
    }
}