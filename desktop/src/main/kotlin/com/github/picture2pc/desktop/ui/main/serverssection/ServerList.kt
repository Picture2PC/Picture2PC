package com.github.picture2pc.desktop.ui.main.serverssection

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.github.picture2pc.common.net2.impl.tcp.ClientState
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel

@Composable
fun ServerList(viewModel: ServersSectionViewModel) {
    val entries by viewModel.serverEntries.collectAsState()

    Column {
        //TODO
        ServerEntry(
            ServersSectionViewModel.ServerEntryState(
                mutableStateOf("TestEntry"),
                "192.168.178.23",
                ClientState.CONNECTED
            )
        )

        entries.forEach {
            ServerEntry(it)
        }
    }
}