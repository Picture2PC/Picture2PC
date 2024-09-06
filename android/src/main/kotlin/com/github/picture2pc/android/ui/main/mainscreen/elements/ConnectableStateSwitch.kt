package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ConnectableStateSwitch(
    viewModel: BroadcastViewModel = rememberKoinInject()
) {
    val connectableSwitch by viewModel.serverConnectable.collectAsState()

    Row {
        Switch(
            checked = connectableSwitch,
            onCheckedChange = { viewModel.saveConnectable(it) }
        )
    }
}