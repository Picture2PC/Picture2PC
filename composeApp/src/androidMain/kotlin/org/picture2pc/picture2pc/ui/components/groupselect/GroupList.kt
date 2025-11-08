package org.picture2pc.picture2pc.ui.components.groupselect

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GroupList(modifier: Modifier = Modifier) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        repeat(10) {
            GroupEntry(
                groupName = "Group Name",
                peersInGroup = 2
            )
        }
    }
}