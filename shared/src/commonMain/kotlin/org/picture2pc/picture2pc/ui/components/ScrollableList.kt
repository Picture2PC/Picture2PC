package org.picture2pc.picture2pc.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.picture2pc.picture2pc.ui.theme.Spacer

@Composable
fun <T> PictureScrollableList(
    modifier: Modifier = Modifier,
    items: List<T>,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable (item: T) -> Unit,
    showGradientOverlay: Boolean = true,
) {
    val state = rememberLazyListState()

    val canScrollDown by remember {
        derivedStateOf { state.canScrollForward }
    }
    val canScrollUp by remember {
        derivedStateOf { state.canScrollBackward }
    }

    val targetBottomRadius = if (canScrollDown) 16.dp else 0.dp
    val animatedBottomRadius by animateDpAsState(
        targetValue = targetBottomRadius,
        animationSpec = tween(durationMillis = 300)
    )
    val animatedBottomAlpha by animateFloatAsState(
        targetValue = if (canScrollDown) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    val targetTopRadius = if (canScrollUp) 16.dp else 0.dp
    val animatedTopRadius by animateDpAsState(
        targetValue = targetTopRadius,
        animationSpec = tween(durationMillis = 300)
    )
    val animatedTopAlpha by animateFloatAsState(
        targetValue = if (canScrollUp) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    val dynamicShape = RoundedCornerShape(
        topStart = animatedTopRadius,
        topEnd = animatedTopRadius,
        bottomEnd = animatedBottomRadius,
        bottomStart = animatedBottomRadius
    )

    Box(
        modifier = modifier.clip(dynamicShape)
    ) {
        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.spacedBy(Spacer.Small)
        ) {
            items(
                items = items,
                key = { item -> key?.invoke(item) ?: item.hashCode() }
            ) { item ->
                itemContent(item)
            }
        }

        if (showGradientOverlay) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .height(50.dp)
                    .graphicsLayer { alpha = animatedTopAlpha }
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.125f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(50.dp)
                    .graphicsLayer { alpha = animatedBottomAlpha }
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.125f)
                            )
                        )
                    )
            )
        }
    }
}