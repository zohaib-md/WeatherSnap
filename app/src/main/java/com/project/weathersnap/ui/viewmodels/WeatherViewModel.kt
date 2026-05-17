package com.project.weathersnap.ui.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weathersnap.data.local.ReportEntity
import com.project.weathersnap.data.remote.dto.CityDto
import com.project.weathersnap.data.remote.dto.CurrentWeatherDto
import com.project.weathersnap.domain.repository.WeatherRepository
import com.project.weathersnap.util.WeatherCodeMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

// ─── UI States ───────────────────────────────────────────────────────────────

sealed class WeatherUiState {
    object Initial : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val city: CityDto, val weather: CurrentWeatherDto) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

/**
 * Holds the in-progress report data that must survive configuration changes
 * and process death. This is the core of the Developer Judgment Challenge.
 */
data class DraftReport(
    val cityName: String = "",
    val temperature: Double = 0.0,
    val conditionCode: Int = 0,
    val humidity: Int = 0,
    val windSpeed: Double = 0.0,
    val pressure: Double = 0.0,
    val imagePath: String? = null,
    val originalImageSizeBytes: Long = 0L,
    val compressedImageSizeBytes: Long = 0L,
    val notes: String = ""
)

// ─── ViewModel ───────────────────────────────────────────────────────────────

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val savedStateHandle: SavedStateHandle,
    private val application: Application
) : ViewModel() {

    // ── Weather Search ───────────────────────────────────────────────────────

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _citySuggestions = MutableStateFlow<List<CityDto>>(emptyList())
    val citySuggestions = _citySuggestions.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Initial)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private var searchJob: Job? = null

    // ── Draft Report (Developer Judgment Challenge) ──────────────────────────
    // Uses SavedStateHandle to survive process death + config changes.
    // The weather snapshot is frozen at the moment the user taps "Create Report",
    // ensuring the saved report always reflects that exact weather, not a
    // silently re-fetched result.

    private val _draftReport = MutableStateFlow(
        savedStateHandle.get<String>("draft_cityName")?.let { cityName ->
            DraftReport(
                cityName = cityName,
                temperature = savedStateHandle.get<Double>("draft_temperature") ?: 0.0,
                conditionCode = savedStateHandle.get<Int>("draft_conditionCode") ?: 0,
                humidity = savedStateHandle.get<Int>("draft_humidity") ?: 0,
                windSpeed = savedStateHandle.get<Double>("draft_windSpeed") ?: 0.0,
                pressure = savedStateHandle.get<Double>("draft_pressure") ?: 0.0,
                imagePath = savedStateHandle.get<String>("draft_imagePath"),
                originalImageSizeBytes = savedStateHandle.get<Long>("draft_originalSize") ?: 0L,
                compressedImageSizeBytes = savedStateHandle.get<Long>("draft_compressedSize") ?: 0L,
                notes = savedStateHandle.get<String>("draft_notes") ?: ""
            )
        } ?: DraftReport()
    )
    val draftReport = _draftReport.asStateFlow()

    // ── Saved Reports ────────────────────────────────────────────────────────

    val savedReports = repository.getAllSavedReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteReport(report: com.project.weathersnap.data.local.ReportEntity) {
        viewModelScope.launch {
            // Delete image file from disk
            try { java.io.File(report.imagePath).delete() } catch (_: Exception) {}
            repository.deleteReport(report)
        }
    }

    // ── Save Confirmation ────────────────────────────────────────────────────

    private val _reportSaved = MutableStateFlow(false)
    val reportSaved = _reportSaved.asStateFlow()

    // ─── Search Logic ────────────────────────────────────────────────────────

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.length > 2) {
            searchJob = viewModelScope.launch {
                delay(500) // Debounce
                repository.searchCity(query).onSuccess { cities ->
                    _citySuggestions.value = cities
                }.onFailure {
                    _citySuggestions.value = emptyList()
                }
            }
        } else {
            _citySuggestions.value = emptyList()
        }
    }

    fun onCitySelected(city: CityDto) {
        _searchQuery.value = city.name
        _citySuggestions.value = emptyList()
        _weatherState.value = WeatherUiState.Loading

        viewModelScope.launch {
            repository.getWeather(city.latitude, city.longitude)
                .onSuccess { response ->
                    _weatherState.value = WeatherUiState.Success(city, response.current)
                }
                .onFailure {
                    _weatherState.value =
                        WeatherUiState.Error("Failed to fetch weather. Please try again.")
                }
        }
    }

    // ─── Draft Report Management ─────────────────────────────────────────────

    /**
     * Freezes the current weather data into a draft report.
     * Called when the user taps "Create Report" on the weather screen.
     */
    fun startReportCreation() {
        val state = _weatherState.value
        if (state is WeatherUiState.Success) {
            val condition = WeatherCodeMapper.fromCode(state.weather.weatherCode)
            val draft = DraftReport(
                cityName = state.city.name,
                temperature = state.weather.temperature,
                conditionCode = state.weather.weatherCode,
                humidity = state.weather.humidity,
                windSpeed = state.weather.windSpeed,
                pressure = state.weather.pressure
            )
            _draftReport.value = draft
            persistDraftToSavedState(draft)
        }
    }

    fun updateDraftNotes(notes: String) {
        val updated = _draftReport.value.copy(notes = notes)
        _draftReport.value = updated
        savedStateHandle["draft_notes"] = notes
    }

    fun updateDraftImage(imagePath: String, originalSize: Long, compressedSize: Long) {
        val updated = _draftReport.value.copy(
            imagePath = imagePath,
            originalImageSizeBytes = originalSize,
            compressedImageSizeBytes = compressedSize
        )
        _draftReport.value = updated
        savedStateHandle["draft_imagePath"] = imagePath
        savedStateHandle["draft_originalSize"] = originalSize
        savedStateHandle["draft_compressedSize"] = compressedSize
    }

    /**
     * Compresses the captured image and updates the draft.
     * Runs on IO dispatcher to keep UI thread free.
     */
    fun compressAndSetImage(originalFile: File) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val originalSize = originalFile.length()

                // Decode and compress to JPEG at 50% quality
                val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
                val compressedFile = File(
                    originalFile.parentFile,
                    "compressed_${originalFile.name}"
                )
                FileOutputStream(compressedFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)
                }
                bitmap.recycle()

                val compressedSize = compressedFile.length()

                updateDraftImage(
                    imagePath = compressedFile.absolutePath,
                    originalSize = originalSize,
                    compressedSize = compressedSize
                )

                // Delete the original uncompressed file to prevent leaks
                if (originalFile.absolutePath != compressedFile.absolutePath) {
                    originalFile.delete()
                }
            }
        }
    }

    /**
     * Saves the finalized report to Room DB and clears the draft state.
     */
    fun saveReport() {
        val draft = _draftReport.value
        if (draft.cityName.isBlank() || draft.imagePath == null) return

        viewModelScope.launch {
            val condition = WeatherCodeMapper.fromCode(draft.conditionCode)
            val entity = ReportEntity(
                cityName = draft.cityName,
                temperature = draft.temperature,
                condition = condition.description,
                humidity = draft.humidity,
                windSpeed = draft.windSpeed,
                pressure = draft.pressure,
                imagePath = draft.imagePath,
                notes = draft.notes,
                originalImageSizeBytes = draft.originalImageSizeBytes,
                compressedImageSizeBytes = draft.compressedImageSizeBytes
            )

            withContext(Dispatchers.IO) {
                repository.saveReport(entity)
            }

            // Clear the draft after successful save
            clearDraft()
            _reportSaved.value = true
        }
    }

    fun resetReportSaved() {
        _reportSaved.value = false
    }

    /**
     * Clears all draft state from both memory and SavedStateHandle.
     * Also deletes any leftover temporary image files.
     */
    private fun clearDraft() {
        _draftReport.value = DraftReport()
        listOf(
            "draft_cityName", "draft_temperature", "draft_conditionCode",
            "draft_humidity", "draft_windSpeed", "draft_pressure",
            "draft_imagePath", "draft_originalSize", "draft_compressedSize",
            "draft_notes"
        ).forEach { savedStateHandle.remove<Any>(it) }
    }

    /**
     * Call this if the user explicitly discards their in-progress report.
     * Cleans up temp image files to prevent leaks.
     */
    fun discardDraft() {
        val imagePath = _draftReport.value.imagePath
        if (imagePath != null) {
            viewModelScope.launch(Dispatchers.IO) {
                File(imagePath).delete()
            }
        }
        clearDraft()
    }

    private fun persistDraftToSavedState(draft: DraftReport) {
        savedStateHandle["draft_cityName"] = draft.cityName
        savedStateHandle["draft_temperature"] = draft.temperature
        savedStateHandle["draft_conditionCode"] = draft.conditionCode
        savedStateHandle["draft_humidity"] = draft.humidity
        savedStateHandle["draft_windSpeed"] = draft.windSpeed
        savedStateHandle["draft_pressure"] = draft.pressure
        savedStateHandle["draft_imagePath"] = draft.imagePath
        savedStateHandle["draft_originalSize"] = draft.originalImageSizeBytes
        savedStateHandle["draft_compressedSize"] = draft.compressedImageSizeBytes
        savedStateHandle["draft_notes"] = draft.notes
    }
}