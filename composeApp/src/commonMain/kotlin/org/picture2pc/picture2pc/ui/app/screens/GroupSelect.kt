package org.picture2pc.picture2pc.ui.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.picture2pc.picture2pc.ui.components.Logo
import org.picture2pc.picture2pc.ui.components.MessagePopup
import org.picture2pc.picture2pc.ui.components.PictureInput
import org.picture2pc.picture2pc.ui.components.PictureScrollableList
import org.picture2pc.picture2pc.ui.components.groupselect.GroupEntry
import org.picture2pc.picture2pc.ui.components.groupselect.GroupSelectNewGroup
import org.picture2pc.picture2pc.ui.components.groupselect.GroupSelectReconnect
import org.picture2pc.picture2pc.ui.theme.CornerRadius
import org.picture2pc.picture2pc.ui.theme.Outline
import org.picture2pc.picture2pc.ui.theme.Padding
import org.picture2pc.picture2pc.ui.theme.Picture2PCTheme
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Typography
import org.picture2pc.picture2pc.ui.theme.Spacer
import org.picture2pc.picture2pc.ui.theme.Theme
import picture2pc.composeapp.generated.resources.Res
import picture2pc.composeapp.generated.resources.group_header
import picture2pc.composeapp.generated.resources.info
import picture2pc.composeapp.generated.resources.input_label
import picture2pc.composeapp.generated.resources.no_group_description
import picture2pc.composeapp.generated.resources.no_group_title

data class PreviousConnection(val group: String, val room: String? = null)

@Composable
@Preview
fun GroupSelect(navController: NavController) {
    val username = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    val previousConnection = PreviousConnection("Zuhause", "Wohnzimmer")

    Picture2PCTheme(Theme.Dark) {
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
                Logo(
                    Modifier
                        .padding(Padding.Container)
                        .fillMaxWidth(), true
                )

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
                    PictureInput(
                        label = stringResource(Res.string.input_label),
                        value = username.value,
                        onValueChange = { username.value = it },
                        onDone = { focusManager.clearFocus() },
                        isError = false,
                        disabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Colors.secondary,
                                CornerRadius.Default
                            )
                            .padding(Padding.Container),
                        verticalArrangement = Arrangement.spacedBy(Spacer.Medium)
                    ) {
                        Text(
                            stringResource(Res.string.group_header),
                            color = Colors.text,
                            style = Typography.titleLarge
                        )
                        Spacer(
                            Modifier
                                .fillMaxWidth()
                                .height(Spacer.XSmall)
                        )
                        GroupSelectNewGroup()

                        PictureScrollableList(
                            modifier = Modifier.weight(1f),
                            items = List(20) { "Room $it" },
                            itemContent = { groupName ->
                                GroupEntry(groupName = groupName, peersInGroup = 2)
                            }
                        )
                        GroupSelectReconnect(previousConnection, navController)
                        // TODO(Show this only once on the first launch (maybe also second))
                        MessagePopup(
                            modifier = Modifier.padding(top = Padding.Small),
                            label = stringResource(Res.string.no_group_title),
                            icon = Res.drawable.info,
                            description = stringResource(Res.string.no_group_description),
                            color = Colors.blue,
                            visible = remember { mutableStateOf(true) }
                        )
                    }
                }
            }
        }
    }
}