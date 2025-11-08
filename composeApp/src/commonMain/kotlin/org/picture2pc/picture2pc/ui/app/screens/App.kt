package org.picture2pc.picture2pc.ui.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.picture2pc.picture2pc.ui.app.viewmodels.AppViewModel
import org.picture2pc.picture2pc.ui.components.ButtonVariant
import org.picture2pc.picture2pc.ui.components.PictureButton
import org.picture2pc.picture2pc.ui.components.PictureIconButton
import org.picture2pc.picture2pc.ui.components.PictureInput
import org.picture2pc.picture2pc.ui.components.PictureSwitch
import org.picture2pc.picture2pc.ui.theme.Picture2PCTheme
import org.picture2pc.picture2pc.ui.theme.PictureTheme
import org.picture2pc.picture2pc.ui.theme.Theme
import picture2pc.composeapp.generated.resources.Res
import picture2pc.composeapp.generated.resources.crop

@Composable
@Preview
fun App(appViewModel: AppViewModel = koinViewModel()) {
    Picture2PCTheme(theme = Theme.Dark) {
        val name by appViewModel.name.collectAsStateWithLifecycle()
        //val connectable by appViewModel.connectable.collectAsStateWithLifecycle()

        val connectable = remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .background(PictureTheme.Colors.background)
                .safeContentPadding()
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { focusManager.clearFocus() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PictureButton(
                onClick = {},
                text = "Picture Button",
                type = ButtonVariant.Primary
            )

            PictureIconButton(
                type = ButtonVariant.Secondary,
                onClick = {},
                icon = Res.drawable.crop,
                iconDescription = "",
                selected = true
            )

            PictureSwitch(
                type = ButtonVariant.Primary,
                checked = connectable.value,
                onCheckedChangeAction = { connectable.value = it },
            )

            PictureInput(
                label = "Name",
                value = name,
                placeholder = "",
                onValueChange = { appViewModel.setName(it) },
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}