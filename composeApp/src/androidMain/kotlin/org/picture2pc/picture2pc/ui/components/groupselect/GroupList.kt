package org.picture2pc.picture2pc.ui.components.groupselect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.picture2pc.picture2pc.ui.theme.CornerRadius

@Composable
fun GroupList(modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(CornerRadius.Default)) {
        LazyColumn {
            items(20) { index ->
                GroupEntry(
                    groupName = "Group $index",
                    peersInGroup = 2
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(32.dp)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.2f))
                    )
                )
        )
    }
}