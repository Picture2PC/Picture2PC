package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import com.github.picture2pc.common.ui.Colors
import org.koin.compose.rememberKoinInject

@Composable
fun ConnectableStateSwitch(
    viewModel: BroadcastViewModel = rememberKoinInject()
) {
    val connectableSwitch by viewModel.serverConnectable.collectAsState()

    Row {
        Switch(
            checked = connectableSwitch,
            onCheckedChange = { viewModel.saveConnectable(it) },
            colors = SwitchDefaults.colors(
                checkedTrackColor = Colors.PRIMARY,
                checkedThumbColor = Colors.TEXT,
                uncheckedTrackColor = Colors.BACKGROUND,
                uncheckedThumbColor = Colors.TEXT,
                uncheckedBorderColor = Colors.TEXT
            )
        )
    }
}