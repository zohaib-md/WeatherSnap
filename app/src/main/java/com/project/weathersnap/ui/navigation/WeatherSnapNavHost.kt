package com.project.weathersnap.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.project.weathersnap.ui.screens.CameraScreen
import com.project.weathersnap.ui.screens.CreateReportScreen
import com.project.weathersnap.ui.screens.SavedReportsScreen
import com.project.weathersnap.ui.screens.SplashScreen
import com.project.weathersnap.ui.screens.WeatherScreen
import com.project.weathersnap.ui.viewmodels.WeatherViewModel

@Composable
fun WeatherSnapNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 } },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 4 } },
        popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { it / 4 } }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Weather.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Weather.route) { backStackEntry ->
            val viewModel: WeatherViewModel = hiltViewModel(backStackEntry)
            val searchQuery by viewModel.searchQuery.collectAsState()
            val suggestions by viewModel.citySuggestions.collectAsState()
            val weatherState by viewModel.weatherState.collectAsState()

            WeatherScreen(
                searchQuery = searchQuery,
                citySuggestions = suggestions,
                weatherState = weatherState,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onCitySelected = viewModel::onCitySelected,
                onCreateReport = {
                    viewModel.startReportCreation()
                    navController.navigate(Screen.CreateReport.route)
                },
                onViewReports = {
                    navController.navigate(Screen.SavedReports.route)
                }
            )
        }

        composable(Screen.CreateReport.route) {
            // Share ViewModel with the Weather screen's back stack entry
            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Screen.Weather.route)
            }
            val viewModel: WeatherViewModel = hiltViewModel(parentEntry)
            val draftReport by viewModel.draftReport.collectAsState()
            val reportSaved by viewModel.reportSaved.collectAsState()

            LaunchedEffect(reportSaved) {
                if (reportSaved) {
                    viewModel.resetReportSaved()
                    navController.navigate(Screen.SavedReports.route) {
                        popUpTo(Screen.Weather.route) { inclusive = false }
                    }
                }
            }

            CreateReportScreen(
                draftReport = draftReport,
                onNotesChange = viewModel::updateDraftNotes,
                onCapturePhoto = {
                    navController.navigate(Screen.Camera.route)
                },
                onSave = viewModel::saveReport,
                onBack = {
                    viewModel.discardDraft()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Camera.route) {
            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Screen.Weather.route)
            }
            val viewModel: WeatherViewModel = hiltViewModel(parentEntry)

            CameraScreen(
                onImageCaptured = { file ->
                    viewModel.compressAndSetImage(file)
                    navController.popBackStack()
                },
                onClose = { navController.popBackStack() }
            )
        }

        composable(Screen.SavedReports.route) {
            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Screen.Weather.route)
            }
            val viewModel: WeatherViewModel = hiltViewModel(parentEntry)
            val reports by viewModel.savedReports.collectAsState()

            SavedReportsScreen(
                reports = reports,
                onDeleteReport = viewModel::deleteReport,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
