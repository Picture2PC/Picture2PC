package com.github.picture2pc.desktop.ui.main.serverssection

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel

@Composable
fun ServerEntry(state: ServersSectionViewModel.ServerEntryState) {
    //TODO Connection state
    Row {
        Text(state.deviceName.value)
        Spacer(Modifier.weight(1f))
        Text(state.deviceAddress)
    }
}