package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.Canvas
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.github.picture2pc.common.net.data.client.ClientState
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.Style
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.rememberKoinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun connectionInfo(
    modifier: Modifier = Modifier,
    serversSectionViewModel: ServersSectionViewModel = rememberKoinInject()
) {
    val availableServers = serversSectionViewModel.availableServers.collectAsState().value
    val scrollState = rememberScrollState()

    Column(modifier = modifier) {
        Text(
            "Connections",
            Modifier.padding(Spacers.NORMAL),
            Colors.TEXT,
            style = TextStyles.HEADER2
        )

        if (availableServers.isEmpty()) {
            Text(
                "No connections",
                Modifier.padding(Spacers.NORMAL),
                Colors.TEXT,
                style = TextStyles.NORMAL
            )
        } else {
            Column(Modifier.verticalScroll(state = scrollState)) {
                availableServers.forEach {
                    connection(it.name, it.deviceState)
                }
            }
        }
        Spacer(Modifier.height(Spacers.NORMAL))
    }
}

@Composable
fun connection(name: StateFlow<String>, clientStateFlow: StateFlow<ClientState>) {
    val clientName = name.collectAsState()
    val clientState = clientStateFlow.collectAsState()
    Row(Modifier.padding(start = Spacers.NORMAL, end = Spacers.NORMAL, top = Spacers.SMALL)) {
        Text(clientName.value, color = Colors.TEXT, style = TextStyles.NORMAL)
        Spacer(Modifier.weight(1f))

        Text(
            clientState.value.displayName,
            color = Colors.TEXT,
            style = TextStyles.NORMAL
        )
        Spacer(Modifier.width(Spacers.SMALL))

        Canvas(Modifier.size(Style.Dimensions.StateIndicator).align(Alignment.CenterVertically)) {
            drawCircle(clientState.value.color)
        }
    }
}
