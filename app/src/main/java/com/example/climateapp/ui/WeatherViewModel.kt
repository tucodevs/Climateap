package com.example.climateapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climateapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weatherInfoState = MutableStateFlow(WeatherInfoState())
    val weatherInfoState: StateFlow<WeatherInfoState> = _weatherInfoState.asStateFlow()

    fun updateWeatherInfo(latitude: Float, longitude: Float) {
        viewModelScope.launch {
            try {
                _weatherInfoState.update { it.copy(isLoading = true, error = null) }
                
                val weatherInfo = repository.getWeatherData(latitude, longitude)
                _weatherInfoState.update {
                    it.copy(
                        weatherInfo = weatherInfo,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _weatherInfoState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to fetch weather data"
                    )
                }
            }
        }
    }

    fun clearError() {
        _weatherInfoState.update { it.copy(error = null) }
    }
}

data class WeatherInfoState(
    val weatherInfo: com.example.climateapp.data.di.module.WeatherInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)