package com.weatherapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.weatherapp.presentation.WeatherScreen
import com.weatherapp.splash.AnimatedSplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Weather : Screen("weather")
    object WeatherDetail : Screen("weather_detail/{cityName}") {
        fun passCity(cityName: String): String {
            return "weather_detail/$cityName"
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
    startDestination: String = Screen.Splash.route
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        }
    ) {
        composable(Screen.Splash.route) {
            AnimatedSplashScreen(
                navController = navController,
                onTimeout = {
                    navController.popBackStack()
                    navController.navigate(Screen.Weather.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Weather.route) {
            WeatherScreen(
                onCitySelected = { cityName ->
                    navController.navigate(Screen.WeatherDetail.passCity(cityName))
                }
            )
        }

        composable(
            route = Screen.WeatherDetail.route,
            arguments = listOf(
                navArgument("cityName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName") ?: "London"
            WeatherScreen(
                initialCity = cityName,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
