package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Heights
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.ui.constants.Settings
import com.github.picture2pc.desktop.viewmodel.mainscreen.ClientPreferencesViewModel
import org.koin.compose.rememberKoinInject

fun nameIsInvalid(name: String) = name.isEmpty() || name.isBlank() || name.length > Settings.MAX_NAME_LENGTH

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NameInputField(
    focusManager: FocusManager,
    clientPreferences: ClientPreferencesViewModel = rememberKoinInject()
) {
    val name = remember { mutableStateOf(clientPreferences.getName()) }
    val isError = remember { mutableStateOf(false) }
    val submitKeys = setOf(Key.Enter, Key.NumPadEnter)

    OutlinedTextField(
        value = name.value,
        onValueChange = { name.value = it },
        placeholder = { Text("Username") },
        label = { Text("Name") },
        modifier = Modifier
            .fillMaxWidth()
            .height(Heights.BUTTON + 10.dp)
            .onKeyEvent { keyEvent ->
                isError.value = nameIsInvalid(name.value)
                if (isError.value) clientPreferences.setConnectable(false)
                else if (keyEvent.key in submitKeys) {
                    clientPreferences.saveName(name.value)
                    focusManager.clearFocus()
                }
                true
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
        isError = isError.value,
    )
}