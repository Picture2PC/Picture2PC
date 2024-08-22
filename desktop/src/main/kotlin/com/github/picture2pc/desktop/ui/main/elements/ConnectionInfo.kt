package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.picture2pc.common.net2.impl.tcp.ClientState
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.Style
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel

@Composable
fun connectionInfo(serversSectionViewModel: ServersSectionViewModel) {
    Column {
        Text(
            "Connections",
            Modifier.padding(Spacers.NORMAL),
            Colors.TEXT,
            style = TextStyles.HEADER2
        )

        if (serversSectionViewModel.serverEntries.collectAsState().value.isEmpty()) {
            Text(
                "No connections",
                Modifier.padding(Spacers.NORMAL),
                Colors.TEXT,
                style = TextStyles.NORMAL
            )
        } else {
            serversSectionViewModel.serverEntries.collectAsState().value.forEach { entry ->
                connection(entry.deviceName, entry.connectionState)
            }
        }
        Spacer(Modifier.height(Spacers.NORMAL))
    }
}

@Composable
fun connection(name: MutableState<String>, state: ClientState) {
    Row(Modifier.padding(start = Spacers.NORMAL, end = Spacers.NORMAL, top = Spacers.SMALL)) {
        Text(name.value, color = Colors.TEXT, style = TextStyles.NORMAL)
        Spacer(Modifier.weight(1f))

        Text(state.displayName, color = Colors.TEXT, style = TextStyles.NORMAL)
        Spacer(Modifier.width(Spacers.SMALL))

        Canvas(Modifier.size(Style.Dimensions.StateIndicator).align(Alignment.CenterVertically)) {
            drawCircle(state.color)
        }
    }
}
