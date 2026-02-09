package org.picture2pc.picture2pc.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Typography
import org.picture2pc.picture2pc.ui.theme.Spacer
import picture2pc.composeapp.generated.resources.Res
import picture2pc.composeapp.generated.resources.app_icon
import picture2pc.composeapp.generated.resources.app_name

@Composable
@Preview
fun Logo(modifier: Modifier = Modifier, showName: Boolean) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painterResource(
                Res.drawable.app_icon
            ),
            stringResource(Res.string.app_name)
        )
        if (showName) {
            Spacer(Modifier.width(Spacer.XLarge))
            Text(
                text = stringResource(Res.string.app_name),
                color = Colors.text,
                style = Typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}