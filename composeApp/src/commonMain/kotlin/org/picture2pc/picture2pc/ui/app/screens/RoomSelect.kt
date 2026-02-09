package org.picture2pc.picture2pc.ui.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.picture2pc.picture2pc.ui.app.routing.Routes
import org.picture2pc.picture2pc.ui.components.ButtonVariant
import org.picture2pc.picture2pc.ui.components.PictureIconButton
import org.picture2pc.picture2pc.ui.components.roomselect.InvitePeers
import org.picture2pc.picture2pc.ui.components.roomselect.RoomList
import org.picture2pc.picture2pc.ui.theme.CornerRadius
import org.picture2pc.picture2pc.ui.theme.Outline
import org.picture2pc.picture2pc.ui.theme.Padding
import org.picture2pc.picture2pc.ui.theme.Picture2PCTheme
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Typography
import org.picture2pc.picture2pc.ui.theme.Spacer
import org.picture2pc.picture2pc.ui.theme.Theme
import picture2pc.composeapp.generated.resources.Res
import picture2pc.composeapp.generated.resources.door_open

@Composable
@Preview
fun RoomSelect(navController: NavController) {
    val focusManager = LocalFocusManager.current

    Picture2PCTheme(theme = Theme.Dark) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Colors.background)
                .displayCutoutPadding()
                .pointerInput(Unit) {
                    detectTapGestures { focusManager.clearFocus() }
                }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(Padding.Small)
                    .border(
                        BorderStroke(Outline.Container, Colors.primary),
                        CornerRadius.Default
                    )
                    .background(Colors.secondary, CornerRadius.Default)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(Padding.Container),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(start = Padding.Small),
                        text = "Zuhause",
                        color = Colors.text,
                        style = Typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    PictureIconButton(
                        modifier = Modifier.size(55.dp),
                        type = ButtonVariant.Red,
                        onClick = { navController.navigate(Routes.GROUP_SELECT) },
                        icon = Res.drawable.door_open,
                        iconDescription = "leave group",
                        disabled = false,
                        selected = false
                    )
                }
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
                    InvitePeers()
                    RoomList()
                }
            }
        }
    }
}
