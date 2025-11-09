package org.picture2pc.picture2pc.ui.components.groupselect

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.pluralStringResource
import org.picture2pc.picture2pc.ui.components.ButtonVariant
import org.picture2pc.picture2pc.ui.components.PictureButton
import org.picture2pc.picture2pc.ui.components.PictureIconButton
import org.picture2pc.picture2pc.ui.theme.PictureTheme
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import picture2pc.composeapp.generated.resources.Res
import picture2pc.composeapp.generated.resources.cancel
import picture2pc.composeapp.generated.resources.peers_in_group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupEntry(
    modifier: Modifier = Modifier,
    groupName: String,
    peersInGroup: Int,
) {
    val peersText = String.format(
        pluralStringResource(Res.plurals.peers_in_group, peersInGroup), peersInGroup
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = groupName,
                color = Colors.text,
                style = PictureTheme.Typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = peersText,
                color = Colors.textDisabled,
                style = PictureTheme.Typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

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