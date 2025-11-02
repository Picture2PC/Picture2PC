package org.picture2pc.picture2pc.ui.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.picture2pc.picture2pc.ui.app.viewmodels.AppViewModel
import org.picture2pc.picture2pc.ui.elements.ButtonType
import org.picture2pc.picture2pc.ui.elements.PictureButton
import org.picture2pc.picture2pc.ui.elements.PictureIconButton
import org.picture2pc.picture2pc.ui.elements.PictureSwitch
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
        val connectable by appViewModel.connectable.collectAsStateWithLifecycle()

        Column(
            modifier = Modifier
                .background(PictureTheme.colors.background)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PictureButton(
                onClick = {},
                text = "Picture Button",
                type = ButtonType.Primary
            )

            PictureIconButton(
                type = ButtonType.Secondary,
                onClick = {},
                icon = Res.drawable.crop,
                iconDescription = "",
                selected = true
            )

            PictureSwitch(
                type = ButtonType.Secondary,
                onCheckedChangeAction = {},
                defaultState = true,
            )

            OutlinedTextField(
                value = name,
                onValueChange = appViewModel::setName
            )
            Spacer(Modifier.height(20.dp))
            Switch(
                connectable,
                appViewModel::setConnectable,
                modifier = Modifier
            )
        }
    }
}