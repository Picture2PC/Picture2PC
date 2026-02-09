package org.picture2pc.picture2pc.ui.components.roomselect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.picture2pc.picture2pc.ui.components.ButtonVariant
import org.picture2pc.picture2pc.ui.components.PictureIconButton
import org.picture2pc.picture2pc.ui.components.PictureScrollableList
import org.picture2pc.picture2pc.ui.theme.CornerRadius
import org.picture2pc.picture2pc.ui.theme.Padding
import org.picture2pc.picture2pc.ui.theme.PictureTheme
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import picture2pc.composeapp.generated.resources.Res
import picture2pc.composeapp.generated.resources.group_add
import picture2pc.composeapp.generated.resources.keyboard_arrow_down
import picture2pc.composeapp.generated.resources.keyboard_arrow_right
import picture2pc.composeapp.generated.resources.room_select_invite_peers

@Composable
fun InvitePeers() {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Colors.secondary, CornerRadius.Default)
            .padding(Padding.Large)
    ) {
        // TODO: replace with expanded value from viewmodel
        var expanded by remember { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.room_select_invite_peers),
                color = Colors.text,
                style = PictureTheme.Typography.titleLarge,
            )
            Spacer(Modifier.weight(1f))
            PictureIconButton(
                type = ButtonVariant.PrimaryDim,
                onClick = { expanded = !expanded },
                icon = if (expanded) Res.drawable.keyboard_arrow_down else Res.drawable.keyboard_arrow_right,
                iconDescription = "expand connected peers list",
                disabled = false,
                selected = false
            )
        }

        if (expanded) {
            BoxWithConstraints {
                val halfHeight = maxHeight / 1.5f

                Column(
                    modifier = Modifier
                        .padding(top = Padding.Small)
                        .heightIn(max = halfHeight)
                ) {
                    //TODO: Replace with actual invitable peers from viewmodel
                    PictureScrollableList(
                        items = List(10) { "Room $it" },
                        itemContent = { peerName ->
                            InvitablePeerEntry(peerName = peerName)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InvitablePeerEntry(peerName: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = peerName,
            color = Colors.text,
            style = PictureTheme.Typography.bodyLarge
        )
        Spacer(Modifier.weight(1f))
        PictureIconButton(
            modifier = Modifier.size(35.dp),
            type = ButtonVariant.Primary,
            onClick = {},
            icon = Res.drawable.group_add,
            iconDescription = "Invite $peerName to group"
        )
    }
}