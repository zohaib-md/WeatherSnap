# WeatherSnap 🌤️📸

A focused Android app that lets users search live weather by city, create weather reports with a custom camera, compress images, save notes, and view saved reports — all built with modern Android architecture.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| DI | Hilt |
| Navigation | Navigation Compose (animated transitions) |
| Network | Retrofit + Gson + OkHttp (debug-only logging) |
| Database | Room (coroutine-based, IO-thread) |
| Camera | CameraX (custom camera, no intent) |
| Async | Coroutines + Flow |

## Setup & Run

1. **Clone** the repository
2. **Open** in Android Studio (Ladybug or newer recommended)
3. **Sync** Gradle — all dependencies will resolve automatically
4. **Run** on a device/emulator with **API 26+** and a camera

> No API key is required. The app uses [Open-Meteo](https://open-meteo.com/) free weather API.

## App Flow

### 1. Weather Screen
- Search for a city (suggestions appear after 3+ characters)
- City suggestions are **cached in-memory** to avoid repeated API calls
- Select a city to view current weather (temperature, condition, humidity, wind speed, pressure)
- Animated loading/success/error/empty states

### 2. Create Report Screen
- Displays the **frozen weather snapshot** from the moment you tapped "Create Report"
- Capture a photo using the custom CameraX camera
- Add notes
- Save to Room database

### 3. Custom Camera Screen
- CameraX-powered live preview (no device camera intent)
- Capture and Close actions
- Images are compressed (JPEG, 50% quality) before saving
- Original and compressed sizes are shown

### 4. Saved Reports Screen
- All saved reports displayed in cards
- Each card shows: captured image, weather details, notes, image sizes, and timestamp
- Empty state when no reports exist

## Architecture

```
com.project.weathersnap/
├── data/
│   ├── local/          # Room DB (Entity, DAO, Database)
│   └── remote/         # Retrofit API services + DTOs
├── di/                 # Hilt modules (Network, Database, Repository)
├── domain/
│   └── repository/     # Repository interface + implementation
├── ui/
│   ├── navigation/     # Screen routes + NavHost
│   ├── screens/        # Compose UI screens
│   ├── theme/          # Material 3 theme
│   └── viewmodels/     # WeatherViewModel (shared across screens)
└── util/               # WeatherCodeMapper utility
```

## Developer Judgment Challenge

### Problem
If the user selects weather, opens the create report screen, captures a photo, enters notes, and then rotates the device or backgrounds/reopens the app before saving, the in-progress report must survive without creating duplicates.

### Approach: SavedStateHandle-based Draft Persistence

I use `SavedStateHandle` in the `WeatherViewModel` to persist all draft report fields (city name, weather data, image path, notes, sizes) across configuration changes and process death.

**Key design decisions:**

1. **Weather snapshot is frozen at report creation time.** When the user taps "Create Report", the current weather data is copied into a `DraftReport` data class and persisted to `SavedStateHandle`. This ensures the saved report always contains the exact weather that was displayed, not a silently re-fetched result.

2. **SavedStateHandle stores individual fields, not a serialized object.** Each field is saved separately (`draft_cityName`, `draft_temperature`, etc.) to keep the implementation simple and avoid custom Parcelable/Serializable boilerplate.

3. **ViewModel is shared across all screens** via the Weather screen's `NavBackStackEntry`. This means the draft state is accessible from the Create Report, Camera, and Saved Reports screens without prop drilling.

4. **Temp file cleanup is explicit.** When a report is successfully saved, the draft state is cleared from both memory and `SavedStateHandle`. When the user discards a draft (navigating back), the temporary image file is deleted to prevent indefinite file leaks.

**Tradeoffs:**
- SavedStateHandle has a ~1MB size limit, but since we only store metadata (not the image bytes), this is well within bounds.
- If the app process dies and the image file is at a path that no longer exists, the draft will restore without the image — the user can recapture. This is an acceptable degradation.
- A more robust approach would use a Room-backed "draft" table with a `DRAFT` status flag, but SavedStateHandle is sufficient for this use case and avoids schema complexity.

## Bonus Points

- ✅ **Debug-only network logging** — OkHttp logging interceptor only runs in debug builds (`BuildConfig.DEBUG` check)
- ✅ **Offline fallback for saved reports** — Reports are stored in Room DB and accessible without internet
- ✅ **IO-thread Room usage** — All DB operations run on `Dispatchers.IO`
- ✅ **In-memory city suggestion cache** — Avoids repeated network calls for the same search query
