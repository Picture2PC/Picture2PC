package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.net.datatransmitter.DataTransmitter
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.rememberKoinInject

@Composable
fun GroupInvitationHandler(
    dataTransmitter: DataTransmitter = rememberKoinInject()
) {
    var currentInvitation by remember { mutableStateOf<Pair<String, TcpPayload.GroupInvitation>?>(null) }

    LaunchedEffect(Unit) {
        dataTransmitter.groupInvitations.collectLatest { invitation ->
            currentInvitation = invitation
        }
    }

    currentInvitation?.let { (deviceName, invitation) ->
        AlertDialog(
            onDismissRequest = { /* Cannot dismiss without action */ },
            title = {
                Text(
                    text = "Group Invitation",
                    color = Colors.TEXT,
                    style = TextStyles.NORMAL
                )
            },
            text = {
                Text(
                    text = "$deviceName wants to invite you to the group \"${invitation.groupName}\"",
                    color = Colors.TEXT,
                    style = TextStyles.NORMAL
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        kotlinx.coroutines.GlobalScope.launch {
                            dataTransmitter.respondToGroupInvitation(invitation, true)
                        }
                        currentInvitation = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Colors.PRIMARY
                    )
                ) {
                    Text("Accept", color = Colors.TEXT, style = TextStyles.NORMAL)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        kotlinx.coroutines.GlobalScope.launch {
                            dataTransmitter.respondToGroupInvitation(invitation, false)
                        }
                        currentInvitation = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Colors.ERROR
                    )
                ) {
                    Text("Decline", color = Colors.TEXT, style = TextStyles.NORMAL)
                }
            },
            containerColor = Colors.BACKGROUND,
            titleContentColor = Colors.TEXT,
            textContentColor = Colors.TEXT
        )
    }
}
