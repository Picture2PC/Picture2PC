package com.github.picture2pc.desktop.ui.main.buttons

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ReloadButton(
    serversSectionViewModel: ServersSectionViewModel = rememberKoinInject()
) {
    Button(onClick = serversSectionViewModel::refreshServers){ Text("Reload")}
}