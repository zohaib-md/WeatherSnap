package com.project.weathersnap.ui.navigation

/**
 * Single source of truth for all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Weather : Screen("weather")
    object CreateReport : Screen("create_report")
    object Camera : Screen("camera")
    object SavedReports : Screen("saved_reports")
}
