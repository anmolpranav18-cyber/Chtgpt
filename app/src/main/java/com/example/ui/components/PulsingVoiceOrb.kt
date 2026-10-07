package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ai.VoiceState
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ResearchBlue
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PulsingVoiceOrb(
    voiceState: VoiceState,
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val primaryColor = when (voiceState) {
        VoiceState.SPEAKING -> EmeraldPrimary
        VoiceState.LISTENING -> ResearchBlue
        VoiceState.THINKING -> Color(0xFF8B5CF6)
        VoiceState.MUTED -> Color(0xFFEF4444)
        else -> EmeraldLight
    }

    val secondaryColor = when (voiceState) {
        VoiceState.SPEAKING -> EmeraldLight
        VoiceState.LISTENING -> Color(0xFF60A5FA)
        VoiceState.THINKING -> Color(0xFFA78BFA)
        VoiceState.MUTED -> Color(0xFFF87171)
        else -> Color.White
    }

    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 3.4f) * (pulseScale + (amplitude * 0.35f))

            // Outer ethereal ripple glow
            for (i in 1..3) {
                val rippleRadius = baseRadius + (i * 22f * (1f + amplitude))
                drawCircle(
                    color = primaryColor.copy(alpha = (0.12f / i) * (amplitude + 0.5f)),
                    radius = rippleRadius,
                    center = center,
                    style = Stroke(width = 3f * i)
                )
            }

            // Radial gradient core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        secondaryColor.copy(alpha = 0.95f),
                        primaryColor.copy(alpha = 0.85f),
                        primaryColor.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.35f
                ),
                radius = baseRadius * 1.35f,
                center = center
            )

            // Inner harmonic orbiting nodes
            val nodeCount = 5
            for (i in 0 until nodeCount) {
                val angleRad = Math.toRadians((rotationAngle + (i * (360.0 / nodeCount))))
                val offsetDist = baseRadius * 0.45f * (0.8f + amplitude * 0.4f)
                val nodeCenter = Offset(
                    x = (center.x + (cos(angleRad) * offsetDist)).toFloat(),
                    y = (center.y + (sin(angleRad) * offsetDist)).toFloat()
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.7f),
                    radius = 8f * (1f + amplitude),
                    center = nodeCenter
                )
            }
        }
    }
}
