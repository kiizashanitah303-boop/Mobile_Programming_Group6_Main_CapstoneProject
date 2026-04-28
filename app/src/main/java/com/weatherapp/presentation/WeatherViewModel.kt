package com.weatherapp.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.data.local.CachedWeather
import com.weatherapp.data.repository.Result
import com.weatherapp.data.repository.WeatherRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherUiState())
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _savedCities = MutableStateFlow<List<CachedWeather>>(emptyList())
    val savedCities: StateFlow<List<CachedWeather>> = _savedCities.asStateFlow()

    private var currentCity: String = ""

    init {
        loadSavedCities()
    }

    fun loadWeather(cityName: String) {
        if (cityName.isBlank()) return

        currentCity = cityName
        viewModelScope.launch {
            _isLoading.value = true
            repository.getWeather(cityName).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _weatherState.value = WeatherUiState(
                            data = result.data,
                            isFromCache = result.isFromCache,
                            error = null
                        )
                        loadSavedCities() // Refresh saved cities list
                    }
                    is Result.Error -> {
                        _weatherState.value = WeatherUiState(
                            data = _weatherState.value.data,
                            isFromCache = false,
                            error = result.error
                        )
                    }
                    is Result.Loading -> {
                        // Loading state handled by _isLoading
                    }
                }
                _isLoading.value = false
            }
        }
    }

    fun refreshWeather(cityName: String) {
        if (cityName.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getWeather(cityName).first()
                when (result) {
                    is Result.Success -> {
                        _weatherState.value = WeatherUiState(
                            data = result.data,
                            isFromCache = false,
                            error = null
                        )
                        loadSavedCities()
                    }
                    is Result.Error -> {
                        _weatherState.value = WeatherUiState(
                            data = _weatherState.value.data,
                            isFromCache = false,
                            error = result.error
                        )
                    }
                    is Result.Loading -> {}
                }
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState(
                    data = _weatherState.value.data,
                    isFromCache = false,
                    error = e.message ?: "Refresh failed"
                )
            }
            _isLoading.value = false
        }
    }

    fun deleteCity(cityName: String) {
        viewModelScope.launch {
            repository.deleteCity(cityName)
            if (currentCity.equals(cityName, ignoreCase = true)) {
                _weatherState.value = WeatherUiState(data = null)
            }
            loadSavedCities()
        }
    }

    private fun loadSavedCities() {
        viewModelScope.launch {
            repository.getAllSavedCities()
                .catch { e -> e.printStackTrace() }
                .collect { cities ->
                    _savedCities.value = cities
                }
        }
    }

    data class WeatherUiState(
        val data: CachedWeather? = null,
        val isFromCache: Boolean = false,
        val error: String? = null
    )
}
