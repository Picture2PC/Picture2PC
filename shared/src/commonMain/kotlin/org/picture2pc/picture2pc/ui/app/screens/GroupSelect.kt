package org.picture2pc.picture2pc.ui.app.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import org.picture2pc.picture2pc.ui.components.AdaptiveSplitScreen
import org.picture2pc.picture2pc.ui.components.InnerCard
import org.picture2pc.picture2pc.ui.components.Logo
import org.picture2pc.picture2pc.ui.components.MessagePopup
import org.picture2pc.picture2pc.ui.components.PictureInput
import org.picture2pc.picture2pc.ui.components.PictureScrollableList
import org.picture2pc.picture2pc.ui.components.SecondaryCard
import org.picture2pc.picture2pc.ui.components.groupselect.GroupEntry
import org.picture2pc.picture2pc.ui.components.groupselect.GroupSelectNewGroup
import org.picture2pc.picture2pc.ui.components.groupselect.GroupSelectReconnect
import org.picture2pc.picture2pc.ui.theme.CornerRadius
import org.picture2pc.picture2pc.ui.theme.Outline
import org.picture2pc.picture2pc.ui.theme.Padding
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Typography
import org.picture2pc.picture2pc.ui.theme.Spacer
import picture2pc.shared.generated.resources.Res
import picture2pc.shared.generated.resources.group_header
import picture2pc.shared.generated.resources.info
import picture2pc.shared.generated.resources.input_label
import picture2pc.shared.generated.resources.no_group_description
import picture2pc.shared.generated.resources.no_group_title
import picture2pc.shared.generated.resources.placeholder_group
import picture2pc.shared.generated.resources.placeholder_room

data class PreviousConnection(val group: String, val room: String? = null)

@Composable
fun GroupSelect(navController: NavController) {
    val username = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val previousConnection = PreviousConnection(
        stringResource(Res.string.placeholder_group),
        stringResource(Res.string.placeholder_room)
    )
    val visiblePopup = remember { mutableStateOf(true) }

    val groupList: @Composable ColumnScope.() -> Unit = {
        Text(
            text = stringResource(Res.string.group_header),
            color = Colors.text,
            style = Typography.titleLarge
        )

        Spacer(Modifier.fillMaxWidth().height(Spacer.XSmall))

        GroupSelectNewGroup()

        PictureScrollableList(
            modifier = Modifier.weight(1f),
            items = List(20) { "Room $it" },
            itemContent = { groupName -> GroupEntry(groupName = groupName, peersInGroup = 2) }
        )
    }

    AdaptiveSplitScreen(
        landscapeLeft = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .border(Outline.Thin, Colors.primary, CornerRadius.Default)
                    .padding(Padding.Medium)
            ) { Logo(showName = true) }

            Spacer(Modifier.height(Spacer.Medium))

            PictureInput(
                label = stringResource(Res.string.input_label),
                value = username.value,
                onValueChange = { username.value = it },
                onDone = { focusManager.clearFocus() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Spacer.Medium))

            GroupSelectReconnect(previousConnection, navController)

            Spacer(Modifier.weight(1f).fillMaxHeight())

            MessagePopup(
                modifier = Modifier.fillMaxWidth().padding(top = Padding.Small),
                label = stringResource(Res.string.no_group_title),
                icon = Res.drawable.info,
                description = stringResource(Res.string.no_group_description),
                color = Colors.blue,
                visible = visiblePopup
            )
        },
        landscapeRight = {
            SecondaryCard(content = groupList)
        },
        portrait = {
            Logo(
                modifier = Modifier
                    .padding(Padding.Container)
                    .fillMaxWidth(),
                showName = true
            )

            InnerCard {
                PictureInput(
                    label = stringResource(Res.string.input_label),
                    value = username.value,
                    onValueChange = { username.value = it },
                    onDone = { focusManager.clearFocus() },
                    modifier = Modifier.fillMaxWidth()
                )

                SecondaryCard {
                    groupList()
                    GroupSelectReconnect(previousConnection, navController)
                    MessagePopup(
                        modifier = Modifier.padding(top = Padding.Small),
                        label = stringResource(Res.string.no_group_title),
                        icon = Res.drawable.info,
                        description = stringResource(Res.string.no_group_description),
                        color = Colors.blue,
                        visible = visiblePopup
                    )
                }
            }
        }
    )
}
