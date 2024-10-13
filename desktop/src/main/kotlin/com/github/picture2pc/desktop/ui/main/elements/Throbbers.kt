package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Style

@Composable
fun LinearThrobber2(
    modifier: Modifier = Modifier,
    dotRadius: Dp = 4.dp,
    spaceBetweenDots: Dp = 4.dp,
    travelDistance: Dp = 8.dp,
    color: Color = Style.Colors.PRIMARY
) {
    val density = LocalDensity.current
    val transition = rememberInfiniteTransition(label = "Infinite Transition")

    val travelDistancePixels = remember { with(density) { travelDistance.toPx() } }

    val dot1Offset by transition.animateFloat(
        initialValue = travelDistancePixels,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 300,
                delayMillis = 250,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(
                offsetMillis = 300,
                offsetType = StartOffsetType.FastForward
            )
        ),
        label = "Dot1 Offset"
    )

    val dot2Offset by transition.animateFloat(
        initialValue = travelDistancePixels,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 300,
                delayMillis = 250,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(
                offsetMillis = 150,
                offsetType = StartOffsetType.FastForward
            )
        ),
        label = "Dot2 Offset"
    )

    val dot3Offset by transition.animateFloat(
        initialValue = travelDistancePixels,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 300,
                delayMillis = 250,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Dot3 Offset"
    )

    Canvas(modifier = modifier) {
        val radius = dotRadius.toPx()
        val gap = radius * 2 + spaceBetweenDots.toPx()
        val widthCenter = size.width / 2
        val heightCenter = size.height / 2
        val dotOffsetY = heightCenter - travelDistancePixels / 2

        drawCircle(
            color = color,
            radius = radius,
            center = Offset(
                widthCenter - gap,
                dotOffsetY + dot1Offset
            )
        )

        drawCircle(
            color = color,
            radius = radius,
            center = Offset(
                widthCenter,
                dotOffsetY + dot2Offset
            )
        )

        drawCircle(
            color = color,
            radius = radius,
            center = Offset(
                widthCenter + gap,
                dotOffsetY + dot3Offset
            )
        )
    }
}