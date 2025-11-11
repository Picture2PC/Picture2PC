package org.picture2pc.picture2pc.ui.components.groupselect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.picture2pc.picture2pc.ui.components.ButtonVariant
import org.picture2pc.picture2pc.ui.components.PictureButton
import org.picture2pc.picture2pc.ui.components.PictureIconButton
import org.picture2pc.picture2pc.ui.theme.PictureTheme
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import picture2pc.composeapp.generated.resources.Res
import picture2pc.composeapp.generated.resources.cancel
import picture2pc.composeapp.generated.resources.group
import picture2pc.composeapp.generated.resources.join
import picture2pc.composeapp.generated.resources.person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupEntry(
    modifier: Modifier = Modifier,
    groupName: String,
    peersInGroup: Int,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = groupName,
                color = Colors.text,
                style = PictureTheme.Typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Text(
                text = peersInGroup.toString(),
                color = Colors.textDisabled,
                style = PictureTheme.Typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                painterResource(if (peersInGroup < 2) Res.drawable.person else Res.drawable.group),
                "peer icon",
                tint = Colors.textDisabled
            )

        }

        PictureButton(
            type = ButtonVariant.Primary,
            onClick = {},
            text = stringResource(Res.string.join),
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