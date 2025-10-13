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
                        val canReceive = client.canReceive.collectAsState().value
                        val groupVerified = client.groupVerified.collectAsState().value
                        
                        Column(
                            modifier = Modifier.padding(vertical = 5.dp)
                        ) {
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
                            Row(
                                modifier = Modifier.padding(top = 5.dp)
                            ) {
                                if (!groupVerified) {
                                    androidx.compose.material3.Button(
                                        onClick = {
                                            viewModel.inviteToGroup(client.uuid)
                                        },
                                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                            containerColor = Colors.PRIMARY
                                        )
                                    ) {
                                        Text(
                                            text = "Invite to Group",
                                            style = TextStyles.SMALL.copy(fontSize = 14.sp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "✓ In Group",
                                        style = TextStyles.SMALL.copy(
                                            fontSize = 14.sp,
                                            color = Colors.PRIMARY
                                        )
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                
                                // Show slider for ALL devices
                                androidx.compose.material3.Switch(
                                    checked = canReceive,
                                    onCheckedChange = { 
                                        viewModel.setDeviceCanReceive(client.uuid, it)
                                    },
                                    modifier = Modifier.height(20.dp),
                                    colors = androidx.compose.material3.SwitchDefaults.colors(
                                        checkedTrackColor = Colors.PRIMARY,
                                        uncheckedTrackColor = Colors.BACKGROUND,
                                        checkedThumbColor = Colors.TEXT,
                                        uncheckedThumbColor = Colors.TEXT.copy(0.5f)
                                    )
                                )
                                Spacer(Modifier.width(Spacers.SMALL))
                                Text(
                                    text = "Receive",
                                    style = TextStyles.SMALL.copy(fontSize = 14.sp),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
