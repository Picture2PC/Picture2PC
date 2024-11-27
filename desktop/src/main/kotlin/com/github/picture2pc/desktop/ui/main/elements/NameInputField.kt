package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Heights
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.ui.constants.Settings
import com.github.picture2pc.desktop.viewmodel.mainscreen.ClientPreferencesViewModel
import org.koin.compose.rememberKoinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NameInputField(
    focusManager: FocusManager,
    viewModel: ClientPreferencesViewModel = rememberKoinInject()
) {
    val name by viewModel.name.collectAsState()
    val isTextFieldError by viewModel.isError.collectAsState()

    OutlinedTextField(
        value = name,
        onValueChange = {
            viewModel.nameChanged(it)
        },
        placeholder = { Text("Username") },
        label = { Text("Name") },
        modifier = Modifier
            .fillMaxWidth()
            .height(Heights.BUTTON + 10.dp)
            .onKeyEvent { keyEvent ->
                if (!isTextFieldError && keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter) {
                    viewModel.saveName(name)
                    focusManager.clearFocus()
                }
                true
            }
            .onGloballyPositioned {
                viewModel.setError(name.length >= Settings.MAX_NAME_LENGTH || name.isEmpty())
            },
        singleLine = true,
        shape = Shapes.BUTTON,
        textStyle = TextStyles.NORMAL,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Colors.PRIMARY,
            unfocusedBorderColor = Colors.PRIMARY,
            cursorColor = Colors.PRIMARY,
            focusedLabelColor = Colors.TEXT,
            unfocusedLabelColor = Colors.TEXT.copy(alpha = 0.8f),
            errorBorderColor = Colors.ERROR
        ),
        isError = isTextFieldError
    )
}