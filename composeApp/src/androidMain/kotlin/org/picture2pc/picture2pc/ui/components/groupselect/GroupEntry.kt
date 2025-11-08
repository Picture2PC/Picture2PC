package org.picture2pc.picture2pc.ui.components.groupselect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.pluralStringResource
import org.picture2pc.picture2pc.ui.components.ButtonVariant
import org.picture2pc.picture2pc.ui.components.PictureButton
import org.picture2pc.picture2pc.ui.components.PictureIconButton
import org.picture2pc.picture2pc.ui.theme.PictureTheme
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.Spacer
import picture2pc.composeapp.generated.resources.Res
import picture2pc.composeapp.generated.resources.cancel
import picture2pc.composeapp.generated.resources.peers_in_group

@Composable
fun GroupEntry(
    modifier: Modifier = Modifier,
    groupName: String,
    peersInGroup: Int,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = groupName, color = Colors.text, style = PictureTheme.Typography.labelLarge)
        Spacer(Modifier.width(Spacer.Small))
        Text(
            text = String.format(
                pluralStringResource(
                    Res.plurals.peers_in_group,
                    peersInGroup
                ),
                peersInGroup
            ),
            color = Colors.text,
            style = PictureTheme.Typography.labelLarge
        )
        Spacer(Modifier.weight(1f))
        PictureButton(
            type = ButtonVariant.Primary,
            onClick = {},
            text = "Join",
            disabled = false,
            selected = false
        )
        PictureIconButton(
            type = ButtonVariant.Red,
            onClick = {},
            icon = Res.drawable.cancel,
            iconDescription = "remove group",
            disabled = false,
            selected = false
        )
    }
}