package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.ClientsViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ConnectedClientsList(
    modifier: Modifier = Modifier,
    viewModel: ClientsViewModel = rememberKoinInject()
) {
    val connections by viewModel.serverEntries.collectAsState(emptyList())

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(25.dp))
            .padding(20.dp),
    ) {
        Row {
            Text(
                text = "Connected Peers",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Row {
            LazyColumn {
                items(connections) { client ->
                    Text(
                        text = client.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
