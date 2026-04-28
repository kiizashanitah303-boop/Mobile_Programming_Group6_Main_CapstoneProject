package com.weatherapp.data.repository

import com.weatherapp.data.local.CachedWeather
import com.weatherapp.data.local.WeatherDao
import com.weatherapp.data.remote.WeatherApiService
import com.weatherapp.data.remote.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherDao: WeatherDao,
    private val apiService: WeatherApiService,
    private val apiKey: String
) {

    companion object {
        private const val CACHE_VALIDITY_MINUTES = 30L
    }

    fun getWeather(cityName: String): Flow<Result<CachedWeather>> = flow {
        emit(Result.loading())

        val normalizedCity = cityName.trim().lowercase()
        val cachedData = weatherDao.getWeatherByCity(normalizedCity).first()

        // Check if cache is valid
        val isCacheValid = cachedData?.let { weather ->
            val timeDiff = Calendar.getInstance().timeInMillis - weather.lastUpdated.time
            timeDiff < CACHE_VALIDITY_MINUTES * 60 * 1000
        } ?: false

        if (isCacheValid && cachedData != null) {
            emit(Result.success(cachedData, isFromCache = true))
        }

        try {
            // Fetch fresh data from API
            val freshData = apiService.getWeather(cityName, apiKey, "metric")
            val cachedEntity = mapToCachedWeather(freshData)

            // Save to database
            weatherDao.insertOrUpdate(cachedEntity)

            // Emit fresh data
            emit(Result.success(cachedEntity, isFromCache = false))
        } catch (e: Exception) {
            if (cachedData == null) {
                emit(Result.error(e.message ?: "Failed to fetch weather data"))
            } else {
                // If we have cached data but network failed, just emit the cached data
                emit(Result.success(cachedData, isFromCache = true))
            }
        }
    }

    fun getWeatherFlow(cityName: String): Flow<CachedWeather?> {
        return weatherDao.getWeatherByCity(cityName.trim().lowercase())
    }

    suspend fun saveFavoriteCity(cityName: String) {
        // This will be saved when we fetch weather for it
        // No separate favorites table needed
    }

    suspend fun deleteCity(cityName: String) {
        weatherDao.deleteCity(cityName.trim().lowercase())
    }

    fun getAllSavedCities(): Flow<List<CachedWeather>> {
        return weatherDao.getAllSavedCities()
    }

    private fun mapToCachedWeather(response: WeatherResponse): CachedWeather {
        return CachedWeather(
            cityName = response.cityName.lowercase(),
            country = response.sys.country,
            temperature = response.main.temperature,
            feelsLike = response.main.feelsLike,
            condition = response.weather.first().condition,
            conditionId = response.weather.first().conditionId,
            humidity = response.main.humidity,
            windSpeed = response.wind.speed,
            weatherIconCode = response.weather.first().iconCode,
            timezoneOffset = response.timezone,
            timestamp = response.timestamp,
            lastUpdated = Date()
        )
    }
}

sealed class Result<T>(val data: T? = null, val error: String? = null, val isLoading: Boolean = false, val isFromCache: Boolean = false) {
    class Success<T>(data: T, isFromCache: Boolean = false) : Result<T>(data = data, isFromCache = isFromCache)
    class Loading<T> : Result<T>(isLoading = true)
    class Error<T>(error: String) : Result<T>(error = error)

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T, isFromCache: Boolean = false) = Success(data, isFromCache)
        fun <T> error(error: String) = Error<T>(error)
    }
}
