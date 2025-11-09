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
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import org.picture2pc.picture2pc.ui.components.Logo
import org.picture2pc.picture2pc.ui.components.MessagePopup
import org.picture2pc.picture2pc.ui.components.PictureInput
import org.picture2pc.picture2pc.ui.components.groupselect.GroupSelectList
import org.picture2pc.picture2pc.ui.components.groupselect.GroupSelectNewGroup
import org.picture2pc.picture2pc.ui.components.groupselect.GroupSelectReconnect
import org.picture2pc.picture2pc.ui.theme.CornerRadius
import org.picture2pc.picture2pc.ui.theme.Outline
import org.picture2pc.picture2pc.ui.theme.Padding
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Typography
import org.picture2pc.picture2pc.ui.theme.Spacer
import picture2pc.composeapp.generated.resources.Res
import picture2pc.composeapp.generated.resources.group_header
import picture2pc.composeapp.generated.resources.info
import picture2pc.composeapp.generated.resources.input_label
import picture2pc.composeapp.generated.resources.input_placeholder
import picture2pc.composeapp.generated.resources.no_group_description
import picture2pc.composeapp.generated.resources.no_group_title

@Composable
@Preview
fun GroupSelectGroupSelectSelect() {
    val username = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

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
                    .padding(Padding.Container)
            ) {
                PictureInput(
                    label = stringResource(Res.string.input_label),
                    placeholder = stringResource(Res.string.input_placeholder),
                    value = username.value,
                    onValueChange = { username.value = it },
                    onDone = { focusManager.clearFocus() },
                    isError = false,
                    disabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(Spacer.Small)
                )
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Colors.secondary,
                            CornerRadius.Default
                        )
                        .padding(Padding.Container),
                    verticalArrangement = Arrangement.spacedBy(Spacer.Small)
                ) {
                    Text(
                        stringResource(Res.string.group_header),
                        color = Colors.text,
                        style = Typography.titleLarge
                    )
                    GroupSelectNewGroup()
                    GroupSelectList(Modifier.weight(1f))
                    GroupSelectReconnect()
                    // TODO(Show this only once on the first launch (maybe also second))
                    MessagePopup(
                        label = stringResource(Res.string.no_group_title),
                        icon = Res.drawable.info,
                        description = stringResource(Res.string.no_group_description),
                        color = Colors.blue,
                    )
                }
            }
        }
    }
}