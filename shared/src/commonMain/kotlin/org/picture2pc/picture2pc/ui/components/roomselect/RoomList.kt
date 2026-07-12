package org.picture2pc.picture2pc.ui.components.roomselect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.picture2pc.picture2pc.ui.components.ButtonVariant
import org.picture2pc.picture2pc.ui.components.PictureButton
import org.picture2pc.picture2pc.ui.components.PictureInputWithAction
import org.picture2pc.picture2pc.ui.components.PictureScrollableList
import org.picture2pc.picture2pc.ui.components.SecondaryCard
import org.picture2pc.picture2pc.ui.theme.PictureTheme
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.Spacer
import picture2pc.shared.generated.resources.Res
import picture2pc.shared.generated.resources.add_circle
import picture2pc.shared.generated.resources.group
import picture2pc.shared.generated.resources.join
import picture2pc.shared.generated.resources.person
import picture2pc.shared.generated.resources.room_select_room_name_input
import picture2pc.shared.generated.resources.room_select_rooms

@Composable
fun RoomList() {
    val focusManager = LocalFocusManager.current
    val roomName = remember { mutableStateOf("") }

    // SecondaryCard with customized padding to match your exact design spec
    SecondaryCard {
        Text(
            text = stringResource(Res.string.room_select_rooms),
            color = Colors.text,
            style = PictureTheme.Typography.titleLarge
        )

        PictureInputWithAction(
            value = roomName.value,
            label = stringResource(Res.string.room_select_room_name_input),
            onValueChange = { roomName.value = it },
            onDone = { focusManager.clearFocus() },
            onActionClick = { /* TODO: Add room logic */ },
            actionIcon = Res.drawable.add_circle,
            actionDescription = "Add new room"
        )

        PictureScrollableList(
            modifier = Modifier.weight(1f, fill = false),
            items = List(10) { "Room $it" },
            itemContent = { roomName ->
                RoomEntry(roomName = roomName, joinedPeers = 3)
            }
        )
    }
}

@Composable
fun RoomEntry(roomName: String, joinedPeers: Int) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacer.Small)
        ) {
            Text(
                text = roomName,
                color = Colors.text,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1f, fill = false)
            )
            Text(
                text = joinedPeers.toString(),
                color = Colors.textDisabled,
                style = PictureTheme.Typography.labelLarge,
            )
            Icon(
                painterResource(if (joinedPeers < 2) Res.drawable.person else Res.drawable.group),
                "peer icon",
                tint = Colors.textDisabled
            )
        }
        PictureButton(
            modifier = Modifier.height(35.dp),
            type = ButtonVariant.Primary,
            onClick = {},
            text = stringResource(Res.string.join),
            disabled = false,
            selected = false
        )
    }
}
