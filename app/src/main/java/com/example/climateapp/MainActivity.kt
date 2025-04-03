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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.climateapp.data.*
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
        Log.d("MainActivity", "Permission result: $permissions")
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Precise location granted
                Log.d("MainActivity", "Location permission granted")
                getLocation()
            }
            else -> {
                // No location access granted
                Log.d("MainActivity", "Location permission denied")
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        locationService = LocationService(this)

        setContent {
            ClimateAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        currentLocation = currentLocation,
                        onGetLocation = { checkLocationPermission() }
                    )
                }
            }
        }

        // Check location permission after UI is set up
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        Log.d("MainActivity", "Checking location permission")
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("MainActivity", "Location permission already granted")
                getLocation()
            }
            else -> {
                Log.d("MainActivity", "Requesting location permission")
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
        Log.d("MainActivity", "Getting location")
        if (!locationService.isLocationEnabled()) {
            Log.d("MainActivity", "Location services are disabled")
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_LONG).show()
            return
        }

        locationService.getLastLocation { location ->
            Log.d("MainActivity", "Location received: $location")
            currentLocation = location
            if (location == null) {
                Toast.makeText(this, "Unable to get location. Please check your GPS settings.", Toast.LENGTH_LONG).show()
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
    // Sample data for preview
    val sampleWeatherData = WeatherData(
        current = CurrentWeather(
            temperature = 25.0,
            humidity = 65,
            windSpeed = 12.0,
            rain = 0.0,
            description = "Partly Cloudy",
            icon = "01d"
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
                date = "Day ${day + 1}",
                maxTemperature = 28.0 + day,
                minTemperature = 20.0 + day,
                icon = "01d"
            )
        }
    )

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
                    Text(
                        text = "Current Location",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = onGetLocation) {
                        Text("Refresh")
                    }
                }
                
                currentLocation?.let { location ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Latitude",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = String.format("%.6f°", location.latitude),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Longitude",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = String.format("%.6f°", location.longitude),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                } ?: Text(
                    text = "Location not available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weather Components
        CurrentWeatherCard(sampleWeatherData.current)
        Spacer(modifier = Modifier.height(16.dp))
        HourlyForecastRow(sampleWeatherData.hourly)
        Spacer(modifier = Modifier.height(16.dp))
        DailyForecastList(sampleWeatherData.daily)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ClimateAppTheme {
        MainScreen(currentLocation = null, onGetLocation = {})
    }
}