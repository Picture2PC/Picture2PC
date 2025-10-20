package org.picture2pc.picture2pc.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.picture2pc.picture2pc.presentation.viewmodel.AppViewModel

@Composable
@Preview
fun App(appViewModel: AppViewModel = koinViewModel()) {
    MaterialTheme {
        val name by appViewModel.name.collectAsStateWithLifecycle()
        val connectable by appViewModel.connectable.collectAsStateWithLifecycle()

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = appViewModel::setName
            )
            Spacer(Modifier.height(20.dp))
            Switch(
                connectable,
                onCheckedChange = appViewModel::setConnectable,
                modifier = Modifier
            )
        }
    }
}