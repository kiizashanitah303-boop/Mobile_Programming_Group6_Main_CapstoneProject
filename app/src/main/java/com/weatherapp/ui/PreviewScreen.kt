package com.weatherapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weatherapp.data.local.CachedWeather
import com.weatherapp.presentation.WeatherContent
import com.weatherapp.ui.theme.WeatherAppTheme
import java.util.Date

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewWeatherScreen() {
    // Create a mock weather data for preview
    val mockWeather = CachedWeather(
        cityName = "london",
        country = "GB",
        temperature = 22.5,
        feelsLike = 21.0,
        condition = "Clear",
        conditionId = 800,
        humidity = 65,
        windSpeed = 5.2,
        weatherIconCode = "01d",
        timezoneOffset = 0,
        timestamp = System.currentTimeMillis() / 1000,
        lastUpdated = Date()
    )

    WeatherAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            WeatherContent(
                weather = mockWeather,
                isFromCache = false,
                currentTime = "14:30",
                currentDate = "Monday, Jan 1",
                onRefresh = {},
                isRefreshing = false
            )
        }
    }
}
