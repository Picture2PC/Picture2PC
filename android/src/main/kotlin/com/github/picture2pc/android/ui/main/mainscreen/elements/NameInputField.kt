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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    val dataName by broadcastViewModel.name.collectAsState()
    val localName = remember { mutableStateOf(broadcastViewModel.name.value) }
    val isError = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        localName.value = dataName
    }

    OutlinedTextField(
        value = localName.value,
        onValueChange = {
            localName.value = it
            isError.value = broadcastViewModel.nameIsInvalid(it)
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
            capitalization = KeyboardCapitalization.Words,
            autoCorrectEnabled = true,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            isError.value = broadcastViewModel.nameIsInvalid(localName.value)
            if (isError.value) broadcastViewModel.setConnectable(false)
            else {
                broadcastViewModel.setName(localName.value)
                broadcastViewModel.savePreferences()
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