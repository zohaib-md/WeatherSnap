package com.project.weathersnap.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.project.weathersnap.R
import com.project.weathersnap.data.remote.dto.CityDto
import com.project.weathersnap.data.remote.dto.CurrentWeatherDto
import com.project.weathersnap.ui.theme.*
import com.project.weathersnap.ui.viewmodels.WeatherUiState
import com.project.weathersnap.util.WeatherCodeMapper

@Composable
fun WeatherScreen(
    searchQuery: String,
    citySuggestions: List<CityDto>,
    weatherState: WeatherUiState,
    onSearchQueryChange: (String) -> Unit,
    onCitySelected: (CityDto) -> Unit,
    onCreateReport: () -> Unit,
    onViewReports: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, Color(0xFF0D1006), DarkBackground)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 52.dp, bottom = 32.dp)
        ) {
            // ── Header Card ──────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(HeaderGradientStart, HeaderGradientMid, HeaderGradientEnd)
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    CardBorderLight.copy(alpha = 0.6f),
                                    CardBorder.copy(alpha = 0.3f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WeatherSnap",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Live weather reports with camera evidence",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Surface(
                            onClick = onViewReports,
                            shape = RoundedCornerShape(24.dp),
                            color = OliveAccent,
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                text = "Reports",
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                fontWeight = FontWeight.Bold,
                                color = OnOliveAccent,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            // ── Search Section ───────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDark)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                label = { Text("City") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OliveAccent,
                                    unfocusedBorderColor = CardBorder,
                                    cursorColor = OliveAccent,
                                    focusedLabelColor = OliveAccent,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                            Surface(
                                onClick = {
                                    if (citySuggestions.isNotEmpty()) {
                                        onCitySelected(citySuggestions.first())
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = OliveAccent,
                                shadowElevation = 2.dp
                            ) {
                                Text(
                                    text = "Search",
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = OnOliveAccent,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "Enter more than 2 letters to start city suggestions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            // ── City Suggestions (Animated) ──────────────────────────────
            if (citySuggestions.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    ) {
                        Column {
                            citySuggestions.forEachIndexed { index, city ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(
                                        animationSpec = tween(200, delayMillis = index * 50)
                                    ) + slideInVertically(
                                        animationSpec = tween(200, delayMillis = index * 50),
                                        initialOffsetY = { -it / 2 }
                                    )
                                ) {
                                    Column {
                                        ListItem(
                                            headlineContent = {
                                                Text(city.name, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                            },
                                            supportingContent = {
                                                val location = listOfNotNull(city.admin1, city.country).joinToString(", ")
                                                if (location.isNotBlank()) Text(location, color = TextSecondary)
                                            },
                                            modifier = Modifier.clickable { onCitySelected(city) },
                                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                        )
                                        if (index < citySuggestions.lastIndex) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                color = CardBorder
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Weather State Content ────────────────────────────────────
            item {
                AnimatedContent(
                    targetState = weatherState,
                    transitionSpec = {
                        fadeIn(tween(400)) + scaleIn(initialScale = 0.96f, animationSpec = tween(400)) togetherWith
                                fadeOut(tween(200))
                    },
                    label = "weather_state"
                ) { state ->
                    when (state) {
                        is WeatherUiState.Initial -> InitialStateContent()
                        is WeatherUiState.Loading -> LoadingStateContent()
                        is WeatherUiState.Success -> WeatherSuccessContent(
                            city = state.city,
                            weather = state.weather,
                            onCreateReport = onCreateReport
                        )
                        is WeatherUiState.Error -> ErrorStateContent(message = state.message)
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

// ─── State Components ────────────────────────────────────────────────────────

@Composable
private fun InitialStateContent() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.map_search))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(vertical = 24.dp, horizontal = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(140.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Search for a city",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "to see current weather conditions",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun LoadingStateContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")

    // Cloud float animation
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -4f, targetValue = 4f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "cloud"
    )
    val cloudScale by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "cloudScale"
    )

    // Three pulsing dots with staggered delays
    val dot1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(600), RepeatMode.Reverse
        ), label = "dot1"
    )
    val dot2 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(600, delayMillis = 200), RepeatMode.Reverse
        ), label = "dot2"
    )
    val dot3 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(600, delayMillis = 400), RepeatMode.Reverse
        ), label = "dot3"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(vertical = 36.dp, horizontal = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Floating weather icon
            Text(
                text = "🌤️",
                fontSize = 44.sp,
                modifier = Modifier
                    .offset(y = cloudOffset.dp)
                    .scale(cloudScale)
            )

            Spacer(Modifier.height(20.dp))

            // Pulsing dots row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(dot1, dot2, dot3).forEach { alpha ->
                    Box(
                        modifier = Modifier
                            .size((6 + 2 * alpha).dp)
                            .clip(CircleShape)
                            .alpha(alpha)
                            .background(OliveAccent)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Fetching weather data",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Connecting to Open-Meteo",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun ErrorStateContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠️", fontSize = 36.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun WeatherSuccessContent(
    city: CityDto,
    weather: CurrentWeatherDto,
    onCreateReport: () -> Unit
) {
    val condition = WeatherCodeMapper.fromCode(weather.weatherCode)

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Main weather card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardDark)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                // City + temperature row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val cityDisplay = listOfNotNull(city.name, city.country).joinToString(", ")
                        Text(
                            text = cityDisplay,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = condition.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    // Temperature badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(OliveAccent, OliveAccentDark)
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "${weather.temperature.toInt()}°C",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnOliveAccent
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Weather detail chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WeatherChip("Humidity", "${weather.humidity}%", ChipTeal, ChipTextTeal, Modifier.weight(1f))
                    WeatherChip("Wind", "${weather.windSpeed} m/s", ChipSlate, ChipTextSlate, Modifier.weight(1f))
                    WeatherChip("Pressure", "${weather.pressure.toInt()}", ChipBrown, ChipTextBrown, Modifier.weight(1f))
                }

                Spacer(Modifier.height(14.dp))

                // Report readiness row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardDarkVariant)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Report readiness",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(StatusGreen)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Camera and Room DB enabled",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Create Report button
        Surface(
            onClick = onCreateReport,
            shape = RoundedCornerShape(16.dp),
            color = OliveAccent,
            shadowElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Create Report",
                    fontWeight = FontWeight.Bold,
                    color = OnOliveAccent,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
private fun WeatherChip(
    label: String,
    value: String,
    chipColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(chipColor)
            .border(1.dp, chipColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}
