package com.github.picture2pc.desktop.ui.main.connectivitybuttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.picture2pc.desktop.ui.main.shape
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ReloadButton(
    serversSectionViewModel: ServersSectionViewModel = rememberKoinInject()
) {
    Button( onClick = serversSectionViewModel::refreshServers, shape = shape, modifier = Modifier.fillMaxWidth())
    { Text("Reload") }
}