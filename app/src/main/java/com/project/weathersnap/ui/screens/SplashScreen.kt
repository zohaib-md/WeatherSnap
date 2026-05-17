package com.project.weathersnap.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.weathersnap.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {

    var phase by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(200)
        phase = 1    // icon
        delay(500)
        phase = 2    // title
        delay(400)
        phase = 3    // tagline
        delay(400)
        phase = 4    // bottom + bar
        delay(1400)
        phase = 5    // exit
        delay(400)
        onSplashComplete()
    }

    // ── Animations ───────────────────────────────────────────────────

    val iconScale by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0.3f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 300f),
        label = "iconScale"
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0f,
        animationSpec = tween(400), label = "iconAlpha"
    )

    val titleOffset by animateFloatAsState(
        targetValue = if (phase >= 2) 0f else 24f,
        animationSpec = tween(600, easing = FastOutSlowInEasing), label = "titleOff"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (phase >= 2) 1f else 0f,
        animationSpec = tween(600), label = "titleAlpha"
    )

    val tagAlpha by animateFloatAsState(
        targetValue = if (phase >= 3) 1f else 0f,
        animationSpec = tween(400), label = "tagAlpha"
    )

    val bottomAlpha by animateFloatAsState(
        targetValue = if (phase >= 4) 1f else 0f,
        animationSpec = tween(400), label = "bottomAlpha"
    )

    val barProgress by animateFloatAsState(
        targetValue = if (phase >= 4) 1f else 0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing), label = "bar"
    )

    val exitAlpha by animateFloatAsState(
        targetValue = if (phase >= 5) 0f else 1f,
        animationSpec = tween(350), label = "exit"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "breatheVal"
    )

    // ── Layout ───────────────────────────────────────────────────────

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(exitAlpha)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1408),
                        Color(0xFF141A0A),
                        Color(0xFF0A0D04)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-30).dp)
        ) {
            // ── Icon with glow ───────────────────────────────────────
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size((100 * breathe).dp)
                        .alpha(0.15f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(OliveAccent, Color.Transparent)
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .scale(iconScale)
                        .alpha(iconAlpha)
                        .size(78.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(OliveAccent, OliveAccentDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⛅", fontSize = 36.sp)
                }
            }

            Spacer(Modifier.height(36.dp))

            // ── Brand Name — CRED-style serif ────────────────────────
            Text(
                text = "Weather",
                modifier = Modifier
                    .alpha(titleAlpha)
                    .offset(y = titleOffset.dp),
                fontFamily = PlayfairDisplay,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = 2.sp
            )
            Text(
                text = "Snap",
                modifier = Modifier
                    .alpha(titleAlpha)
                    .offset(y = titleOffset.dp),
                fontFamily = PlayfairDisplay,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                color = OliveAccent,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(16.dp))

            // ── Tagline ──────────────────────────────────────────────
            Text(
                text = "CAPTURE  ·  COMPRESS  ·  REPORT",
                modifier = Modifier.alpha(tagAlpha),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 4.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Real-time weather intelligence\nwith camera evidence",
                modifier = Modifier.alpha(tagAlpha),
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )

            Spacer(Modifier.height(48.dp))

            // ── Loading bar ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(2.dp)
                    .alpha(bottomAlpha)
                    .clip(RoundedCornerShape(1.dp))
                    .background(CardDarkVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = barProgress)
                        .clip(RoundedCornerShape(1.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(OliveAccent, OliveAccentLight)
                            )
                        )
                )
            }
        }

        // ── Bottom ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp)
                .alpha(bottomAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "POWERED BY",
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "Open-Meteo  ·  CameraX  ·  Room DB",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }
    }
}
