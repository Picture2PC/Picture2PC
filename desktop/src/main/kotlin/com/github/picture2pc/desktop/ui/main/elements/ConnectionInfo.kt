package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.picture2pc.common.net.defaultdatatransmitter.DefaultDevice
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.Style
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.viewmodel.mainscreen.BroadcastViewModel
import com.github.picture2pc.desktop.viewmodel.mainscreen.ServersSectionViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ConnectionInfo(
    modifier: Modifier = Modifier,
    serversSectionViewModel: ServersSectionViewModel = rememberKoinInject(),
    broadcastViewModel: BroadcastViewModel = rememberKoinInject()
) {
    val availableServers = serversSectionViewModel.availableServers.collectAsState().value
    val scrollState = rememberScrollState()
    val connectable = broadcastViewModel.getConnectable()

    Column(modifier = modifier) {
        Text(
            "Connections",
            Modifier.padding(Spacers.NORMAL),
            Colors.TEXT,
            style = TextStyles.HEADER2
        )

        if (availableServers.isEmpty()) {
            Text(
                if (connectable) "No connections" else "Not connectable",
                Modifier.padding(Spacers.NORMAL),
                Colors.TEXT,
                style = TextStyles.NORMAL
            )
        } else {
            Column(Modifier.verticalScroll(state = scrollState)) {
                availableServers.forEach {
                    Connection(it, serversSectionViewModel)
                }
            }
        }
        Spacer(Modifier.height(Spacers.NORMAL))
    }
}

@Composable
fun Connection(device: DefaultDevice, viewModel: ServersSectionViewModel) {
    val clientName = device.name.collectAsState()
    val clientState = device.deviceState.collectAsState()
    val canReceive = device.canReceive.collectAsState()
    val groupVerified = device.groupVerified.collectAsState()
    
    Column(
        Modifier.padding(
            start = Spacers.NORMAL,
            end = Spacers.NORMAL,
            top = Spacers.SMALL
        )
    ) {
        Row {
            Text(clientName.value, color = Colors.TEXT, style = TextStyles.NORMAL)
            Spacer(Modifier.weight(1f))

            Text(
                clientState.value.displayName,
                color = Colors.TEXT,
                style = TextStyles.NORMAL
            )
            Spacer(Modifier.width(Spacers.SMALL))

            Canvas(
                Modifier
                    .size(Style.Dimensions.StateIndicator)
                    .align(Alignment.CenterVertically)
            ) {
                drawCircle(clientState.value.color)
            }
        }
        
        Row(
            Modifier.padding(top = Spacers.SMALL)
        ) {
            Text(
                if (groupVerified.value) "✓ In Group" else "✗ Not in Group",
                color = if (groupVerified.value) Colors.PRIMARY else Colors.TEXT.copy(0.5f),
                style = TextStyles.SMALL
            )
            Spacer(Modifier.weight(1f))
            
            if (groupVerified.value) {
                androidx.compose.material3.Switch(
                    checked = canReceive.value,
                    onCheckedChange = { 
                        viewModel.setDeviceCanReceive(device.uuid, it)
                    },
                    modifier = Modifier.height(20.dp),
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedTrackColor = Colors.PRIMARY,
                        uncheckedTrackColor = Colors.BACKGROUND,
                        checkedThumbColor = Colors.TEXT,
                        uncheckedThumbColor = Colors.TEXT.copy(0.5f)
                    )
                )
                Spacer(Modifier.width(Spacers.SMALL))
                Text(
                    "Receive",
                    color = Colors.TEXT,
                    style = TextStyles.SMALL,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}
