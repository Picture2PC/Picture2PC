package org.picture2pc.picture2pc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.picture2pc.picture2pc.ui.theme.CornerRadius

@Composable
fun PictureScrollableList(modifier: Modifier = Modifier, listItems: Unit) {
    Box(modifier = modifier.clip(CornerRadius.Default)) {
        val state = rememberScrollState()

        Column(Modifier.verticalScroll(state)) { listItems }
        if (state.canScrollForward)
            Box(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(50.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(0.2f)
                            )
                        )
                    )
            )
    }
}
