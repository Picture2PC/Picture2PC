package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ConnectableStateSwitch(
    modifier: Modifier,
    viewModel: BroadcastViewModel = rememberKoinInject()
) {

    val connectableSwitch by viewModel.serverConnectable.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
    ) {
        Text(
            fontSize = 20.sp,
            text = "Connectable:",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.5F)
        )
        Switch(
            checked = connectableSwitch,
            onCheckedChange = { viewModel.saveConnectable(it) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}