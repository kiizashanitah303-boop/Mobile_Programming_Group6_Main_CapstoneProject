package com.weatherapp.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

object WeatherConditionMapper {

    // Returns ImageVector instead of Int resource ID
    fun getWeatherIcon(conditionId: Int): ImageVector {
        return when (conditionId) {
            in 200..232 -> Icons.Default.FlashOn  // Storm
            in 300..321 -> Icons.Default.Grain    // Drizzle
            in 500..531 -> Icons.Default.BeachAccess  // Rain
            in 600..622 -> Icons.Default.AcUnit   // Snow
            in 701..781 -> Icons.Default.Circle   // Mist
            800 -> Icons.Default.WbSunny          // Clear
            in 801..804 -> Icons.Default.Cloud    // Clouds
            else -> Icons.Default.WbSunny
        }
    }

    fun getWeatherConditionText(conditionId: Int): String {
        return when (conditionId) {
            in 200..232 -> "Thunderstorm ⚡"
            in 300..321 -> "Drizzle 🌧️"
            in 500..531 -> "Rain ☔"
            in 600..622 -> "Snow ❄️"
            in 701..781 -> "Mist 🌫️"
            800 -> "Clear Sky ☀️"
            801 -> "Few Clouds 🌤️"
            802 -> "Scattered Clouds ☁️"
            803 -> "Broken Clouds ☁️"
            804 -> "Overcast Clouds ☁️"
            else -> "Unknown Weather"
        }
    }

    fun getGradientColors(conditionId: Int, isDayTime: Boolean = true): Pair<Color, Color> {
        return when {
            conditionId == 800 && isDayTime -> Pair(
                Color(0xFF4A90E2),
                Color(0xFF87CEEB)
            )
            conditionId == 800 && !isDayTime -> Pair(
                Color(0xFF1A1A2E),
                Color(0xFF16213E)
            )
            conditionId in 801..804 -> Pair(
                Color(0xFF757F9A),
                Color(0xFFD7DDE8)
            )
            conditionId in 500..531 -> Pair(
                Color(0xFF2C3E50),
                Color(0xFF3498DB)
            )
            conditionId in 600..622 -> Pair(
                Color(0xFF83A4D4),
                Color(0xFFB6FBFF)
            )
            else -> Pair(
                Color(0xFF4A90E2),
                Color(0xFF87CEEB)
            )
        }
    }
}
