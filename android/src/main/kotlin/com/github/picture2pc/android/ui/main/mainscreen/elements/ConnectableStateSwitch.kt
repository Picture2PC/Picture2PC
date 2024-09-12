package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.TextStyles
import org.koin.compose.rememberKoinInject

@Composable
fun ConnectableStateSwitch(
    viewModel: BroadcastViewModel = rememberKoinInject()
) {
    val connectableSwitch by viewModel.connectable.collectAsState()

    Row {
        Switch(
            checked = connectableSwitch,
            onCheckedChange = { viewModel.saveConnectable(it) },
            colors = SwitchDefaults.colors(
                checkedTrackColor = Colors.PRIMARY,
                checkedThumbColor = Colors.TEXT,
                uncheckedTrackColor = Colors.BACKGROUND,
                uncheckedThumbColor = Colors.TEXT.copy(0.5f),
                uncheckedBorderColor = Colors.TEXT.copy(0.5f)
            )
        )
        Spacer(Modifier.width(10.dp))
        Text(
            "Connectable",
            style = TextStyles.NORMAL.copy(fontSize = 20.sp),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}