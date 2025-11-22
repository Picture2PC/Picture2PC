package org.picture2pc.picture2pc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.picture2pc.picture2pc.ui.theme.CornerRadius
import org.picture2pc.picture2pc.ui.theme.Padding
import org.picture2pc.picture2pc.ui.theme.Spacer

@Composable
fun <T> PictureScrollableList(
    modifier: Modifier = Modifier,
    items: List<T>,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable (item: T) -> Unit,
    showGradientOverlay: Boolean = true,
) {
    Box(
        modifier = modifier.clip(CornerRadius.RoundedBottomDefault)
    ) {
        val state = rememberLazyListState()
        val canScroll by remember {
            derivedStateOf { state.canScrollForward }
        }

        LazyColumn(
            state = state,
            modifier = Modifier.padding(top = Padding.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacer.Small)
        ) {
            items(
                items = items,
                key = { item -> key?.invoke(item) ?: item.hashCode() }
            ) { item ->
                itemContent(item)
            }
        }

        if (showGradientOverlay && canScroll) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(50.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.2f)
                            )
                        )
                    )
            )
        }
    }
}
