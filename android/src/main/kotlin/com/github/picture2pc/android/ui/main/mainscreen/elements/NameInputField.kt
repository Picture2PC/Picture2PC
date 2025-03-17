package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Heights
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.TextStyles
import org.koin.compose.rememberKoinInject

@Composable
fun NameInputField(
    modifier: Modifier = Modifier,
    broadcastViewModel: BroadcastViewModel = rememberKoinInject(),
) {
    val focusManager = LocalFocusManager.current
    val name = remember { mutableStateOf("") }
    val isError = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        name.value = broadcastViewModel.name
    }

    OutlinedTextField(
        value = name.value,
        onValueChange = {
            name.value = it
            isError.value = broadcastViewModel.nameIsInvalid(name.value)
        },
        placeholder = { Text("Username") },
        label = { Text("Name") },
        modifier = modifier
            .fillMaxWidth()
            .height(Heights.BUTTON + 10.dp),
        singleLine = true,
        shape = Shapes.BUTTON,
        textStyle = TextStyles.NORMAL,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrectEnabled = true,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            isError.value = broadcastViewModel.nameIsInvalid(name.value)
            if (isError.value) broadcastViewModel.setConnectable(false)
            else {
                broadcastViewModel.saveName(name.value)
                focusManager.clearFocus()
            }
        }),
        colors = OutlinedTextFieldDefaults.colors(
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