package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.ClientsViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ConnectedClientsList(modifier: Modifier, viewModel: ClientsViewModel = rememberKoinInject()) {

    val connections by viewModel.serverEntries.collectAsState()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp, 0.dp)
    ) {
        items(connections) { client ->
            Client(clientData = client)
            Divider(modifier = Modifier.padding(0.dp, 5.dp), thickness = 2.dp)
        }
    }
}
