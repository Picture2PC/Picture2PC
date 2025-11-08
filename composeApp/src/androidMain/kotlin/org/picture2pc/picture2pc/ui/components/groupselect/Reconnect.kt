package org.picture2pc.picture2pc.ui.components.groupselect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import org.picture2pc.picture2pc.ui.components.ButtonVariant
import org.picture2pc.picture2pc.ui.components.PictureButton
import org.picture2pc.picture2pc.ui.theme.CornerRadius
import org.picture2pc.picture2pc.ui.theme.Padding
import org.picture2pc.picture2pc.ui.theme.PictureTheme
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import picture2pc.composeapp.generated.resources.Res
import picture2pc.composeapp.generated.resources.reconnect_label

@Composable
fun GroupSelectReconnect() {
    Column(
        Modifier
            .background(
                Colors.primary.copy(alpha = 0.25f),
                CornerRadius.Default
            )
            .padding(bottom = Padding.Small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PictureButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(bottom = Padding.Small),
            type = ButtonVariant.Primary,
            onClick = {},
            text = stringResource(Res.string.reconnect_label),
            disabled = false,
            selected = false
        )
        Text(
            "Group name / Room name",
            color = Colors.text,
            style = PictureTheme.Typography.labelSmall
        )
    }
}