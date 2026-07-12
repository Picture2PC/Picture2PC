package org.picture2pc.picture2pc.ui.components.groupselect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import org.jetbrains.compose.resources.stringResource
import org.picture2pc.picture2pc.ui.components.ButtonVariant
import org.picture2pc.picture2pc.ui.components.PictureIconButton
import org.picture2pc.picture2pc.ui.components.PictureInput
import org.picture2pc.picture2pc.ui.theme.Spacer
import picture2pc.shared.generated.resources.Res
import picture2pc.shared.generated.resources.add_circle
import picture2pc.shared.generated.resources.group_label

@Composable
fun GroupSelectNewGroup() {
    val groupName = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacer.Small)
    ) {
        PictureInput(
            modifier = Modifier.weight(1f),
            label = stringResource(Res.string.group_label),
            value = groupName.value,
            onValueChange = { groupName.value = it },
            onDone = { focusManager.clearFocus() },
            isError = false,
            disabled = false,
        )
        PictureIconButton(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .width(IntrinsicSize.Min),
            type = ButtonVariant.Primary,
            onClick = {},
            icon = Res.drawable.add_circle,
            iconDescription = "Create new group",
            disabled = false,
            selected = false
        )
    }
}