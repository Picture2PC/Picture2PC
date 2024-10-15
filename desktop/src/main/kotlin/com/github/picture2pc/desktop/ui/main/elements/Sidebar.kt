package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Heights
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.ui.constants.Descriptions
import com.github.picture2pc.desktop.ui.constants.Settings
import com.github.picture2pc.desktop.ui.main.elements.sidebar.ConnectionInfo
import com.github.picture2pc.desktop.ui.main.elements.sidebar.Header
import com.github.picture2pc.desktop.ui.main.elements.sidebar.ImageInteractionButtons
import com.github.picture2pc.desktop.viewmodel.clientviewmodel.ClientViewModel
import kotlinx.coroutines.launch
import org.koin.compose.rememberKoinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sidebar(
    clientViewModel: ClientViewModel = rememberKoinInject(),
    serverPreferencesRepository: ServerPreferencesRepository = rememberKoinInject(),
) {
    val showConnections = remember { mutableStateOf(true) }
    val clientName by clientViewModel.clientName.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    var textFieldError by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxHeight()
            .width(Settings.SIDEBAR_WIDTH.dp)
            .background(Colors.SECONDARY, Shapes.WINDOW)
            .clickable(interactionSource = interactionSource, indication = null) {
                focusManager.clearFocus()
                clientViewModel.viewModelScope.launch {
                    serverPreferencesRepository.setName(clientViewModel.clientName.value)
                }
            }
    ) {
        // Items in the Sidebar
        Column(Modifier.padding(Spacers.NORMAL).fillMaxSize()) {
            Header()
            Spacer(Modifier.height(Spacers.LARGE))

            OutlinedTextField(
                value = clientName,
                onValueChange = {
                    if (it.length >= Settings.MAX_NAME_LENGTH) {
                        textFieldError = true
                        return@OutlinedTextField
                    } else textFieldError = false
                    if (it.isEmpty()) textFieldError = true
                    clientViewModel.saveClientName(it)
                },
                placeholder = { Text("Unknown") },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Heights.BUTTON + 10.dp)
                    .focusRequester(focusRequester),
                singleLine = true,
                shape = Shapes.BUTTON,
                textStyle = TextStyles.NORMAL,
                keyboardOptions = KeyboardOptions.Default,
                keyboardActions = KeyboardActions(onDone = {
                    clientViewModel.viewModelScope.launch {
                        serverPreferencesRepository.setName(clientName)
                        focusManager.clearFocus()
                    }
                }),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Colors.PRIMARY,
                    unfocusedBorderColor = Colors.PRIMARY,
                    cursorColor = Colors.PRIMARY,
                    focusedLabelColor = Colors.TEXT,
                    unfocusedLabelColor = Colors.TEXT.copy(alpha = 0.8f),
                    errorBorderColor = Colors.ERROR
                ),
                isError = textFieldError
            )
            Spacer(Modifier.height(Spacers.LARGE))

            Row { ImageInteractionButtons() }
            Spacer(Modifier.height(Spacers.LARGE))

            // CONNECTION INFO
            if (showConnections.value) {
                ConnectionInfo(
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(Colors.ACCENT, Shapes.BUTTON)
                )
                Spacer(Modifier.height(Spacers.NORMAL))
            } else Spacer(Modifier.weight(1f))

            // Connection Info Toggle Button
            Box(Modifier.fillMaxWidth()) {
                TooltipIconButton(
                    Modifier.align(Alignment.BottomEnd),
                    Descriptions.INFO,
                    Icons.Desktop.INFO,
                    Colors.ACCENT,
                ) { showConnections.value = !showConnections.value }
            }
        }
    }
}