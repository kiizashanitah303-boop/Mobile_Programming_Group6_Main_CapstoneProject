package com.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cached_weather")
data class CachedWeather(
    @PrimaryKey
    val cityName: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val condition: String,
    val conditionId: Int,
    val humidity: Int,
    val windSpeed: Double,
    val weatherIconCode: String,
    val timezoneOffset: Int,
    val timestamp: Long,
    val lastUpdated: Date
)

data class FavoriteCity(
    val cityName: String,
    val country: String
)
