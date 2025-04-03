package com.example.climateapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.climateapp.data.*
import com.example.climateapp.ui.WeatherViewModel
import com.example.climateapp.ui.components.*
import com.example.climateapp.ui.theme.ClimateAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var locationService: LocationService
    private var currentLocation by mutableStateOf<Location?>(null)

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.d("ClimaApp", "Resultado da permissão: $permissions")
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Log.d("ClimaApp", "Permissão de localização concedida")
                getLocation()
            }
            else -> {
                Log.d("ClimaApp", "Permissão de localização negada")
                Toast.makeText(this, "A permissão de localização é obrigatória", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ClimaApp", "MainActivity criada")
        enableEdgeToEdge()

        locationService = LocationService(this)

        setContent {
            ClimateAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Log.d("ClimaApp", "Renderizando MainScreen...")
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        currentLocation = currentLocation,
                        onGetLocation = { checkLocationPermission() }
                    )
                }
            }
        }

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        Log.d("ClimaApp", "Verificando permissão de localização")
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("ClimaApp", "Permissão já concedida")
                getLocation()
            }
            else -> {
                Log.d("ClimaApp", "Solicitando permissão de localização")
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getLocation() {
        Log.d("ClimaApp", "Obtendo localização atual")
        if (!locationService.isLocationEnabled()) {
            Log.d("ClimaApp", "Serviços de localização estão desativados")
            Toast.makeText(this, "Ative os serviços de localização", Toast.LENGTH_LONG).show()
            return
        }

        locationService.getLastLocation { location ->
            Log.d("ClimaApp", "Localização recebida: $location")
            currentLocation = location
            if (location == null) {
                Toast.makeText(this, "Não foi possível obter a localização. Verifique o GPS.", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    currentLocation: Location?,
    onGetLocation: () -> Unit
) {
    val viewModel: WeatherViewModel = viewModel()
    val state = viewModel.weatherInfoState.collectAsState().value

    LaunchedEffect(currentLocation) {
        Log.d("ClimaApp", "LaunchedEffect acionado. Localização = $currentLocation")
        currentLocation?.let {
            Log.d("ClimaApp", "Chamando ViewModel com lat=${it.latitude}, lon=${it.longitude}")
            viewModel.updateWeatherInfo(it.latitude.toFloat(), it.longitude.toFloat())
        }
    }

    val weatherData = state.weatherInfo?.let {
        WeatherData(
            current = CurrentWeather(
                temperature = it.temperature ?: 0.0,
                humidity = it.humidity ?: 0,
                windSpeed = it.windSpeed ?: 0.0,
                rain = it.rain ?: 0.0,
                description = it.condition ?: "N/A",
                icon = it.Icon ?: "01d"
            ),
            hourly = List(6) { hour ->
                HourlyForecast(
                    time = "${hour + 1}:00",
                    temperature = 25.0 + hour,
                    icon = "01d"
                )
            },
            daily = List(7) { day ->
                DailyForecast(
                    date = "Dia ${day + 1}",
                    maxTemperature = 28.0 + day,
                    minTemperature = 20.0 + day,
                    icon = "01d"
                )
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Location Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Localização Atual", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = onGetLocation) { Text("Atualizar") }
                }

                currentLocation?.let { location ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Latitude", style = MaterialTheme.typography.bodyMedium)
                            Text(String.format("%.6f°", location.latitude), style = MaterialTheme.typography.titleMedium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Longitude", style = MaterialTheme.typography.bodyMedium)
                            Text(String.format("%.6f°", location.longitude), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                } ?: Text(
                    text = "Localização não disponível",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        weatherData?.let {
            Log.d("ClimaApp", "Renderizando dados do clima com API")
            CurrentWeatherCard(it.current)
            Spacer(modifier = Modifier.height(16.dp))
            HourlyForecastRow(it.hourly)
            Spacer(modifier = Modifier.height(16.dp))
            DailyForecastList(it.daily)
        }


    }
}
