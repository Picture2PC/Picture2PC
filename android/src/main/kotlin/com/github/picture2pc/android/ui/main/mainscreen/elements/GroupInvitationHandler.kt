package com.github.picture2pc.android.ui.main.mainscreen.elements

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
import androidx.compose.ui.unit.sp
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.TextStyles
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
                    style = TextStyles.NORMAL.copy(fontSize = 20.sp)
                )
            },
            text = {
                Text(
                    text = "$deviceName wants to invite you to the group \"${invitation.groupName}\"",
                    style = TextStyles.NORMAL.copy(fontSize = 16.sp)
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
                    Text("Accept", style = TextStyles.NORMAL)
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
                    Text("Decline", style = TextStyles.NORMAL)
                }
            },
            containerColor = Colors.BACKGROUND,
            titleContentColor = Colors.TEXT,
            textContentColor = Colors.TEXT
        )
    }
}
