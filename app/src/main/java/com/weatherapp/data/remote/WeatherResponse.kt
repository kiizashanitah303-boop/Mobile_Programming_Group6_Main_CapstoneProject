package com.weatherapp.data.remote


import com.google.gson.annotations.SerializedName
import java.util.Date

data class WeatherResponse(
    @SerializedName("name")
    val cityName: String,
    @SerializedName("sys")
    val sys: Sys,
    @SerializedName("main")
    val main: Main,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("wind")
    val wind: Wind,
    @SerializedName("dt")
    val timestamp: Long,
    @SerializedName("timezone")
    val timezone: Int
)

data class Sys(
    @SerializedName("country")
    val country: String,
    @SerializedName("sunrise")
    val sunrise: Long,
    @SerializedName("sunset")
    val sunset: Long
)

data class Main(
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("pressure")
    val pressure: Int
)

data class Weather(
    @SerializedName("id")
    val conditionId: Int,
    @SerializedName("main")
    val condition: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val iconCode: String
)

data class Wind(
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("deg")
    val degree: Int
)
