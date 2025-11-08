package org.picture2pc.picture2pc.ui.components.groupselect

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.picture2pc.picture2pc.ui.components.Logo
import org.picture2pc.picture2pc.ui.theme.Padding

@Composable
fun GroupSelectHeader() {
    Logo(
        Modifier
            .padding(Padding.Container)
            .fillMaxWidth(), true
    )
}