package com.project.weathersnap.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.weathersnap.ui.theme.*
import com.project.weathersnap.ui.viewmodels.DraftReport
import com.project.weathersnap.util.WeatherCodeMapper
import java.io.File

@Composable
fun CreateReportScreen(
    draftReport: DraftReport,
    onNotesChange: (String) -> Unit,
    onCapturePhoto: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val condition = WeatherCodeMapper.fromCode(draftReport.conditionCode)
    val canSave = draftReport.imagePath != null && draftReport.cityName.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, Color(0xFF0D1006), DarkBackground)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 52.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Header Card ──────────────────────────────────────────────
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
                        1.dp,
                        Brush.horizontalGradient(
                            colors = listOf(CardBorderLight.copy(alpha = 0.6f), CardBorder.copy(alpha = 0.3f))
                        ),
                        RoundedCornerShape(20.dp)
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
                            text = "Create Report",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Capture, compress, annotate",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Surface(
                        onClick = onBack,
                        shape = RoundedCornerShape(24.dp),
                        color = CardDarkElevated
                    ) {
                        Text(
                            text = "Back",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // ── Weather Snapshot Card ────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = draftReport.cityName,
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
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.verticalGradient(listOf(OliveAccent, OliveAccentDark))
                                )
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "${draftReport.temperature.toInt()}°C",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = OnOliveAccent
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReportChip("Humidity", "${draftReport.humidity}%", ChipTeal, ChipTextTeal, Modifier.weight(1f))
                        ReportChip("Wind", "${draftReport.windSpeed} m/s", ChipSlate, ChipTextSlate, Modifier.weight(1f))
                        ReportChip("Pressure", "${draftReport.pressure.toInt()}", ChipBrown, ChipTextBrown, Modifier.weight(1f))
                    }
                }
            }

            // ── Photo Preview Card ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    AnimatedContent(
                        targetState = draftReport.imagePath,
                        transitionSpec = {
                            fadeIn(tween(400)) + scaleIn(initialScale = 0.92f, animationSpec = tween(400)) togetherWith
                                    fadeOut(tween(200))
                        },
                        label = "image_preview"
                    ) { imagePath ->
                        if (imagePath != null && File(imagePath).exists()) {
                            Column {
                                val bitmap = remember(imagePath) { BitmapFactory.decodeFile(imagePath) }
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Captured photo",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(14.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CardDarkVariant)
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Original: ${formatFileSize(draftReport.originalImageSizeBytes)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        "Compressed: ${formatFileSize(draftReport.compressedImageSizeBytes)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = OliveAccent
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                OliveAccent.copy(alpha = 0.15f),
                                                CardDarkVariant
                                            )
                                        )
                                    )
                                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Rounded.CameraAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp),
                                        tint = TextMuted
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Photo preview",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Surface(
                        onClick = onCapturePhoto,
                        shape = RoundedCornerShape(14.dp),
                        color = OliveAccent,
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (draftReport.imagePath != null) "Retake Photo" else "Capture Photo",
                                fontWeight = FontWeight.Bold,
                                color = OnOliveAccent,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            }

            // ── Field Notes Card ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "Field Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = draftReport.notes,
                        onValueChange = onNotesChange,
                        placeholder = { Text("Notes", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OliveAccent,
                            unfocusedBorderColor = CardBorder,
                            cursorColor = OliveAccent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }

            // ── Save Report Button ───────────────────────────────────────
            Surface(
                onClick = { if (canSave) onSave() },
                shape = RoundedCornerShape(16.dp),
                color = if (canSave) OliveAccent else OliveAccent.copy(alpha = 0.3f),
                shadowElevation = if (canSave) 6.dp else 0.dp,
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Save Report",
                        fontWeight = FontWeight.Bold,
                        color = if (canSave) OnOliveAccent else OnOliveAccent.copy(alpha = 0.4f),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ReportChip(
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
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(3.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
        else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }
}
