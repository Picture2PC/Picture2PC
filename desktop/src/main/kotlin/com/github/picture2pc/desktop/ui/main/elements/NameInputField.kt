package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Heights
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.ui.constants.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.rememberKoinInject
import org.koin.core.qualifier.named

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInputField(
    serverPreferencesRepository: ServerPreferencesRepository = rememberKoinInject(),
    coroutineScope: CoroutineScope = rememberKoinInject(named("viewModelCoroutineScope"))
) {
    var name by remember { mutableStateOf("") }
    var isTextFieldError by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = name,
        onValueChange = {
            if (it.length >= Settings.MAX_NAME_LENGTH) {
                isTextFieldError = true
                coroutineScope.launch {
                    serverPreferencesRepository.setConnectable(!isTextFieldError)
                }
                return@OutlinedTextField
            } else {
                isTextFieldError = false
                name = it
                coroutineScope.launch {
                    serverPreferencesRepository.setConnectable(!isTextFieldError)
                }
            }
            if (it.isEmpty()) {
                isTextFieldError = true
                coroutineScope.launch {
                    serverPreferencesRepository.setConnectable(!isTextFieldError)
                }
            }
        },
        placeholder = { Text("Unknown") },
        label = { Text("Name") },
        modifier = Modifier
            .fillMaxWidth()
            .height(Heights.BUTTON + 10.dp),
        singleLine = true,
        shape = Shapes.BUTTON,
        textStyle = TextStyles.NORMAL,
        keyboardOptions = KeyboardOptions.Default,
        keyboardActions = KeyboardActions(onDone = {
            println("Done")
            coroutineScope.launch {
                serverPreferencesRepository.setName(name)
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
        isError = isTextFieldError
    )
}