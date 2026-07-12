package org.picture2pc.picture2pc.ui.app.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.stringResource
import org.picture2pc.picture2pc.ui.app.routing.Routes
import org.picture2pc.picture2pc.ui.components.AdaptiveSplitScreen
import org.picture2pc.picture2pc.ui.components.ButtonVariant
import org.picture2pc.picture2pc.ui.components.InnerCard
import org.picture2pc.picture2pc.ui.components.PictureIconButton
import org.picture2pc.picture2pc.ui.components.roomselect.InvitePeers
import org.picture2pc.picture2pc.ui.components.roomselect.RoomList
import org.picture2pc.picture2pc.ui.theme.CornerRadius
import org.picture2pc.picture2pc.ui.theme.Outline
import org.picture2pc.picture2pc.ui.theme.Padding
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.Spacer
import picture2pc.shared.generated.resources.Res
import picture2pc.shared.generated.resources.door_open
import picture2pc.shared.generated.resources.placeholder_room

@Composable
fun RoomSelect(navController: NavController) {
    AdaptiveSplitScreen(
        landscapeLeft = { _ ->
            ScreenHeader(
                Modifier.border(Outline.Thin, Colors.primary, CornerRadius.Default),
                title = stringResource(Res.string.placeholder_room),
                padding = Padding.Medium,
                onBackClick = { navController.navigate(Routes.GROUP_SELECT) }
            )

            Spacer(Modifier.height(Spacer.Medium))

            InvitePeers(
                Modifier.fillMaxHeight().border(Outline.Thin, Colors.primary, CornerRadius.Default),
                alwaysExpanded = true
            )
        },
        landscapeRight = {
            RoomList()
        },
        portrait = {
            ScreenHeader(
                title = stringResource(Res.string.placeholder_room),
                onBackClick = { navController.navigate(Routes.GROUP_SELECT) }
            )

            InnerCard {
                InvitePeers()
                Box(Modifier.fillMaxWidth()) { RoomList() }
            }
        }
    )
}

@Composable
fun ScreenHeader(
    modifier: Modifier = Modifier,
    title: String,
    padding: Dp = Padding.Container,
    onBackClick: () -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(start = Padding.Small),
            text = title,
            color = Colors.text,
            style = Typography().headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.weight(1f))
        PictureIconButton(
            modifier = Modifier.size(55.dp),
            type = ButtonVariant.Red,
            onClick = onBackClick,
            icon = Res.drawable.door_open,
            iconDescription = "leave group",
            disabled = false,
            selected = false
        )
    }
}
