package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.ClientsViewModel
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.TextStyles
import org.koin.compose.rememberKoinInject

@Composable
fun ConnectedClientsList(
    modifier: Modifier = Modifier,
    viewModel: ClientsViewModel = rememberKoinInject()
) {
    val connections by viewModel.serverEntries.collectAsState(emptyList())

    Column(
        modifier = modifier
            .background(Colors.ACCENT, RoundedCornerShape(25.dp))
            .padding(20.dp),
    ) {
        Row {
            Text(
                text = "Connections",
                style = TextStyles.NORMAL.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            if (connections.isEmpty()) {
                Text(
                    text = "No connections",
                    style = TextStyles.NORMAL.copy(fontSize = 20.sp)
                )
            } else {
                LazyColumn {
                    items(connections) { client ->
                        val clientName = client.name.collectAsState().value
                        val clientState = client.deviceState.collectAsState().value
                        Row {
                            Text(
                                text = clientName,
                                style = TextStyles.NORMAL.copy(fontSize = 20.sp)
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = clientState.displayName,
                                modifier = Modifier.align(Alignment.CenterVertically),
                                style = TextStyles.SMALL.copy(fontSize = 16.sp)
                            )
                            Spacer(Modifier.width(Spacers.SMALL))
                            Box(
                                Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(clientState.color)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
    }
}
