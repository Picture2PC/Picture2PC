package org.picture2pc.picture2pc.ui.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.picture2pc.picture2pc.domain.repository.net.client.ClientState
import org.picture2pc.picture2pc.ui.components.ButtonVariant
import org.picture2pc.picture2pc.ui.components.PictureIconButton
import org.picture2pc.picture2pc.ui.components.PictureScrollableList
import org.picture2pc.picture2pc.ui.theme.CornerRadius
import org.picture2pc.picture2pc.ui.theme.Outline
import org.picture2pc.picture2pc.ui.theme.Padding
import org.picture2pc.picture2pc.ui.theme.Picture2PCTheme
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Typography
import org.picture2pc.picture2pc.ui.theme.Spacer
import org.picture2pc.picture2pc.ui.theme.Theme
import picture2pc.composeapp.generated.resources.Res
import picture2pc.composeapp.generated.resources.door_open
import picture2pc.composeapp.generated.resources.keyboard_arrow_down
import picture2pc.composeapp.generated.resources.keyboard_arrow_right
import picture2pc.composeapp.generated.resources.room_select_connected_peers
import picture2pc.composeapp.generated.resources.sports_martial_arts

@Composable
@Preview
fun RoomSelect() {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Picture2PCTheme(theme = Theme.Dark) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Colors.background)
                .displayCutoutPadding()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { focusManager.clearFocus() }
                )
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(Padding.Small)
                    .border(
                        BorderStroke(Outline.Container, Colors.primary),
                        CornerRadius.Default
                    )
                    .background(
                        Colors.secondary, CornerRadius.Default
                    )
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(Padding.Container),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Zuhause",
                        color = Colors.text,
                        style = Typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    PictureIconButton(
                        modifier = Modifier.size(55.dp),
                        type = ButtonVariant.Red,
                        onClick = {},
                        icon = Res.drawable.door_open,
                        iconDescription = "leave group",
                        disabled = false,
                        selected = false
                    )
                }
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Colors.background,
                            CornerRadius.Default
                        )
                        .border(
                            BorderStroke(Outline.Container, Colors.primary),
                            CornerRadius.Default
                        )
                        .padding(Padding.Container),
                    verticalArrangement = Arrangement.spacedBy(Spacer.Large)
                ) {
                    // Connected Peers Component
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(Colors.secondary, CornerRadius.Default)
                            .padding(
                                start = Padding.Container,
                                end = Padding.Medium,
                                top = Padding.Large,
                                bottom = Padding.Large
                            )
                    ) {
                        // Connected Peers Header + Expand / Hide Button
                        var expanded by remember { mutableStateOf(false) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.room_select_connected_peers),
                                color = Colors.text,
                                style = Typography.titleLarge,
                            )
                            Spacer(Modifier.weight(1f))
                            PictureIconButton(
                                type = ButtonVariant.PrimaryDim,
                                onClick = {expanded = !expanded},
                                icon = if (expanded) Res.drawable.keyboard_arrow_down else Res.drawable.keyboard_arrow_right,
                                iconDescription = "expand connected peers list",
                                disabled = false,
                                selected = false
                            )
                        }
                        // Connected Peers List (with admin option to kick)
                        if (expanded) {
                            Column {
                                PictureScrollableList(listItems = repeat(50) {
                                    ConnectedPeerEntry(
                                        peerName = "Joe Mama",
                                        status = ClientState.CONNECTED,
                                        isAdmin = false
                                    )
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectedPeerEntry(peerName: String, status: ClientState, isAdmin: Boolean) {
    Row(
        modifier = Modifier.padding(Padding.Small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacer.Medium)
    ) {
        Text(
            text = peerName,
            color = Colors.text,
            style = Typography.bodyLarge,

            )
        Spacer(Modifier.weight(1f))
        Text(
            text = status.displayName,
            color = Colors.text,
            style = Typography.bodySmall,

            )
        Box(
            Modifier
                .size(7.5.dp)
                .clip(CircleShape)
                .background(status.color)
                .align(Alignment.CenterVertically)
        )
        if (isAdmin) {
            PictureIconButton(
                type = ButtonVariant.Red,
                onClick = {},
                icon = Res.drawable.sports_martial_arts,
                iconDescription = "kick $peerName",
                disabled = false,
                selected = false
            )
        } else {
            Spacer(Modifier.width(Padding.Medium))
        }
    }
}
