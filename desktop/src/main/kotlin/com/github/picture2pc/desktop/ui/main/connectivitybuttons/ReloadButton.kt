package com.github.picture2pc.desktop.ui.main.connectivitybuttons

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ReloadButton(
    serversSectionViewModel: ServersSectionViewModel = rememberKoinInject()
) {
    Button( onClick = serversSectionViewModel::refreshServers )
    { Text("Reload") }
}