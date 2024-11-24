package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.data.preferences.PreferencesRepository
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Heights
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.ui.constants.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.rememberKoinInject
import org.koin.core.qualifier.named

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NameInputField(
    focusManager: FocusManager,
    preferencesRepository: PreferencesRepository = rememberKoinInject(),
    coroutineScope: CoroutineScope = rememberKoinInject(named("viewModelCoroutineScope"))
) {
    var name by remember { mutableStateOf(preferencesRepository.name.value) }
    var isTextFieldError by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = name,
        onValueChange = {
            if (it.length >= Settings.MAX_NAME_LENGTH) {
                return@OutlinedTextField
            } else {
                isTextFieldError = false
                name = it
            }
            if (it.isEmpty()) {
                name = it
                isTextFieldError = true
            }
            coroutineScope.launch {
                preferencesRepository.setConnectable(!isTextFieldError)
            }
        },
        placeholder = { Text("Username") },
        label = { Text("Name") },
        modifier = Modifier
            .fillMaxWidth()
            .height(Heights.BUTTON + 10.dp)

            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Enter && !isTextFieldError) {
                    coroutineScope.launch {
                        name = name.trim()
                        preferencesRepository.setName(name)
                    }
                    focusManager.clearFocus()
                }
                true
            }

            .onGloballyPositioned {
                isTextFieldError = name.length >= Settings.MAX_NAME_LENGTH || name.isEmpty()
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