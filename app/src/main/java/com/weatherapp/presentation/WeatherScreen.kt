package com.weatherapp.presentation

import com.weatherapp.R
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weatherapp.data.local.CachedWeather
import com.weatherapp.ui.theme.WeatherAppTheme
import com.weatherapp.utils.TimeUtils
import com.weatherapp.utils.WeatherConditionMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun WeatherScreen(
    initialCity: String = "London",
    onCitySelected: ((String) -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val savedCitiesState by viewModel.savedCities.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var searchText by remember { mutableStateOf(initialCity) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var showSavedCities by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadWeather(initialCity)
    }

    LaunchedEffect(weatherState.data?.timezoneOffset) {
        while (true) {
            weatherState.data?.let { weather ->
                currentTime = TimeUtils.getCurrentTimeForTimezone(weather.timezoneOffset)
                currentDate = TimeUtils.getCurrentDateForTimezone(weather.timezoneOffset)
            }
            delay(60000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = getAnimatedBackgroundBrush(
                    conditionId = weatherState.data?.conditionId ?: 800,
                    isDayTime = isDayTime(weatherState.data?.timezoneOffset ?: 0)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            AnimatedTopBar(
                searchText = searchText,
                isSearchExpanded = isSearchExpanded,
                onSearchTextChange = { searchText = it },
                onSearch = {
                    keyboardController?.hide()
                    viewModel.loadWeather(searchText)
                    onCitySelected?.invoke(searchText)
                    isSearchExpanded = false
                },
                onExpandToggle = { isSearchExpanded = !isSearchExpanded },
                onShowSavedCities = { showSavedCities = true }
            )

            when {
                isLoading && weatherState.data == null -> {
                    LoadingShimmerEffect()
                }
                weatherState.error != null && weatherState.data == null -> {
                    ErrorState(
                        errorMessage = weatherState.error ?: "Unknown error",
                        onRetry = { viewModel.loadWeather(searchText) }
                    )
                }
                weatherState.data != null -> {
                    WeatherContent(
                        weather = weatherState.data!!,
                        isFromCache = weatherState.isFromCache,
                        currentTime = currentTime,
                        currentDate = currentDate,
                        onRefresh = { viewModel.refreshWeather(searchText) },
                        isRefreshing = isLoading
                    )
                }
                else -> {
                    EmptyState()
                }
            }
        }

        if (showSavedCities) {
            SavedCitiesDialog(
                cities = savedCitiesState,
                onCitySelected = { city ->
                    showSavedCities = false
                    searchText = city.cityName
                    viewModel.loadWeather(city.cityName)
                    onCitySelected?.invoke(city.cityName)
                },
                onDismiss = { showSavedCities = false },
                onDeleteCity = { city ->
                    scope.launch {
                        viewModel.deleteCity(city.cityName)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedTopBar(
    searchText: String,
    isSearchExpanded: Boolean,
    onSearchTextChange: (String) -> Unit,
    onSearch: () -> Unit,
    onExpandToggle: () -> Unit,
    onShowSavedCities: () -> Unit
) {
    AnimatedContent(
        targetState = isSearchExpanded,
        transitionSpec = {
            fadeIn() with fadeOut() using SizeTransform(clip = true)
        }
    ) { expanded ->
        if (expanded) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onExpandToggle) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = onSearchTextChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Enter city name...") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    )

                    IconButton(onClick = onSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }

                    IconButton(onClick = onShowSavedCities) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .animateContentSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WeatherWise",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Row {
                    IconButton(onClick = onShowSavedCities) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    IconButton(onClick = onExpandToggle) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherContent(
    weather: CachedWeather,
    isFromCache: Boolean,
    currentTime: String,
    currentDate: String,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    var animatedTemperature by remember { mutableStateOf(weather.temperature.toInt()) }

    LaunchedEffect(weather.temperature) {
        val targetTemp = weather.temperature.toInt()
        val step = if (targetTemp > animatedTemperature) 1 else -1
        while (animatedTemperature != targetTemp) {
            animatedTemperature += step
            delay(8)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isFromCache) {
            item {
                OfflineIndicator()
            }
        }

        item {
            AnimatedWeatherHeader(
                cityName = weather.cityName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                country = weather.country,
                currentDate = currentDate,
                currentTime = currentTime
            )
        }

        item {
            AnimatedWeatherDisplay(
                conditionId = weather.conditionId,
                temperature = animatedTemperature,
                condition = weather.condition
            )
        }

        item {
            WeatherDetails(
                feelsLike = weather.feelsLike,
                humidity = weather.humidity,
                windSpeed = weather.windSpeed
            )
        }

        item {
            LastUpdatedInfo(
                lastUpdated = weather.lastUpdated,
                onRefresh = onRefresh,
                isRefreshing = isRefreshing
            )
        }
    }
}

@Composable
fun AnimatedWeatherHeader(
    cityName: String,
    country: String,
    currentDate: String,
    currentTime: String
) {
    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "header_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = cityName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = getFlagEmoji(country),
                fontSize = 24.sp
            )
        }

        Text(
            text = currentDate,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            modifier = Modifier.padding(top = 4.dp)
        )

        AnimatedClockDisplay(time = currentTime)
    }
}

@Composable
fun AnimatedClockDisplay(time: String) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Text(
        text = time,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.scale(pulse)
    )
}

@Composable
fun AnimatedWeatherDisplay(
    conditionId: Int,
    temperature: Int,
    condition: String
) {
    val weatherIconRes = WeatherConditionMapper.getWeatherIcon(conditionId)
    val conditionText = WeatherConditionMapper.getWeatherConditionText(conditionId)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.WbSunny,  // ✅ Valid icon
                contentDescription = "Weather Icon"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = temperature.toString(),
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "°C",
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = conditionText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = getFriendlyMessage(conditionId),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun WeatherDetails(
    feelsLike: Double,
    humidity: Int,
    windSpeed: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem(
                icon = Icons.Default.Thermostat,
                value = "${feelsLike.toInt()}°C",
                label = "Feels Like",
                color = Color(0xFFFF6B6B)
            )

            WeatherDetailItem(
                icon = Icons.Default.WaterDrop,
                value = "$humidity%",
                label = "Humidity",
                color = Color(0xFF4ECDC4)
            )

            WeatherDetailItem(
                icon = Icons.Default.Air,
                value = "${windSpeed.toInt()} km/h",
                label = "Wind Speed",
                color = Color(0xFF45B7D1)
            )
        }
    }
}

@Composable
fun WeatherDetailItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun LastUpdatedInfo(
    lastUpdated: Date,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Updated ${TimeUtils.getRelativeTimeString(lastUpdated)}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        val rotation by animateFloatAsState(
            targetValue = if (isRefreshing) 360f else 0f,
            animationSpec = tween(500),
            label = "refresh_rotation"
        )

        IconButton(
            onClick = onRefresh,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
fun OfflineIndicator() {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFF9800).copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(pulse)
                    .background(Color.White, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Showing cached data - You're offline",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun LoadingShimmerEffect() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            repeat(3) { index ->
                val bounce by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = repeatable(
                        iterations = Int.MAX_VALUE,
                        animation = keyframes {
                            durationMillis = 600
                            0.8f at 0 with LinearEasing
                            1.2f at 300 with EaseOut
                            1f at 600 with EaseIn
                        }
                    ),
                    label = "bounce_dot_$index"
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .scale(bounce)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                )
            }
        }

        Text(
            text = "Loading weather data...",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp
        )
    }
}

@Composable
fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var shake by remember { mutableStateOf(0f) }

        LaunchedEffect(Unit) {
            shake = 10f
            delay(100)
            shake = -10f
            delay(100)
            shake = 5f
            delay(100)
            shake = -5f
            delay(100)
            shake = 0f
        }

        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(64.dp)
                .offset(x = shake.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Oops! Something went wrong",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val floatOffset by animateFloatAsState(
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float"
        )

        // Temporarily use a Text emoji instead of image to avoid R error
        Text(
            text = "☁️",
            fontSize = 80.sp,
            modifier = Modifier.offset(y = floatOffset.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "✨ Enter a city name to see magic ✨",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Search for any city worldwide to get real-time weather updates",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SavedCitiesDialog(
    cities: List<CachedWeather>,
    onCitySelected: (CachedWeather) -> Unit,
    onDismiss: () -> Unit,
    onDeleteCity: (CachedWeather) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Saved Cities")
            }
        },
        text = {
            if (cities.isEmpty()) {
                Text("No saved cities yet. Search for a city to save it automatically!")
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cities.forEach { city ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCitySelected(city) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = city.cityName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${city.temperature.toInt()}°C - ${city.condition}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteCity(city) }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// Helper functions
fun getFlagEmoji(countryCode: String): String {
    val code = countryCode.uppercase()
    return String(intArrayOf(0x1F1E6 + code[0].code - 'A'.code, 0x1F1E6 + code[1].code - 'A'.code), 0, 2)
}

fun isDayTime(timezoneOffset: Int): Boolean {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.add(Calendar.SECOND, timezoneOffset)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    return hour in 6..18
}

fun getFriendlyMessage(conditionId: Int): String {
    return when (conditionId) {
        800 -> "Perfect day to go outside! ☀️"
        in 801..804 -> "Nice weather for outdoor activities 🌤️"
        in 500..531 -> "Don't forget your umbrella! ☔"
        in 600..622 -> "Winter wonderland! ❄️"
        in 200..232 -> "Stay safe indoors! ⚡"
        else -> "Check the weather before heading out! 📱"
    }
}

@Composable
fun getAnimatedBackgroundBrush(conditionId: Int, isDayTime: Boolean): Brush {
    val (startColor, endColor) = WeatherConditionMapper.getGradientColors(conditionId, isDayTime)

    val infiniteTransition = rememberInfiniteTransition()
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    return Brush.linearGradient(
        colors = listOf(startColor, endColor, startColor),
        start = Offset(offsetX, 0f),
        end = Offset(offsetX + 1000f, 1000f)
    )
}
