package org.picture2pc.picture2pc.ui.components.groupselect

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.picture2pc.picture2pc.ui.theme.CornerRadius as DisplayRadius

@Composable
fun GroupList(modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(DisplayRadius.Default)) {
        val state = rememberScrollState()

        Column(Modifier.verticalScroll(state)) {
            repeat(5) { index ->
                GroupEntry(
                    groupName = "Group Name",
                    peersInGroup = 2
                )
            }
        }
    }
}