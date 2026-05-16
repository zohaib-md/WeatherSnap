package com.project.weathersnap.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weathersnap.data.remote.dto.CityDto
import com.project.weathersnap.data.remote.dto.CurrentWeatherDto
import com.project.weathersnap.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Defining our UI States
sealed class WeatherUiState {
    object Initial : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val city: CityDto, val weather: CurrentWeatherDto) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _citySuggestions = MutableStateFlow<List<CityDto>>(emptyList())
    val citySuggestions = _citySuggestions.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Initial)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        // Requirement: Fetch suggestions after more than 2 letters
        if (query.length > 2) {
            searchJob = viewModelScope.launch {
                delay(500) // Debounce so we don't spam the API on every single keystroke
                repository.searchCity(query).onSuccess { cities ->
                    _citySuggestions.value = cities
                }.onFailure {
                    // Handle search error silently or show a toast
                    _citySuggestions.value = emptyList()
                }
            }
        } else {
            _citySuggestions.value = emptyList()
        }
    }

    fun onCitySelected(city: CityDto) {
        _searchQuery.value = city.name
        _citySuggestions.value = emptyList() // Hide dropdown
        _weatherState.value = WeatherUiState.Loading

        viewModelScope.launch {
            repository.getWeather(city.latitude, city.longitude)
                .onSuccess { response ->
                    _weatherState.value = WeatherUiState.Success(city, response.current)
                }
                .onFailure {
                    _weatherState.value = WeatherUiState.Error("Failed to fetch weather. Please try again.")
                }
        }
    }
}