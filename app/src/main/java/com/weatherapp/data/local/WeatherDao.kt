package com.weatherapp.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(weather: CachedWeather)

    @Query("SELECT * FROM cached_weather WHERE cityName = :cityName")
    fun getWeatherByCity(cityName: String): Flow<CachedWeather?>

    @Query("SELECT * FROM cached_weather ORDER BY lastUpdated DESC")
    fun getAllSavedCities(): Flow<List<CachedWeather>>

    @Query("DELETE FROM cached_weather WHERE lastUpdated < :cutoffDate")
    suspend fun deleteOldEntries(cutoffDate: Date)

    @Query("DELETE FROM cached_weather WHERE cityName = :cityName")
    suspend fun deleteCity(cityName: String)

    @Query("SELECT COUNT(*) FROM cached_weather")
    suspend fun getCount(): Int
}
