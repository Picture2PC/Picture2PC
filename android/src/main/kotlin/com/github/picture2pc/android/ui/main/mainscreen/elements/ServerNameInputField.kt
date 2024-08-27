package com.github.picture2pc.android.ui.main.mainscreen.elements


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import com.github.picture2pc.common.ui.Colors
import org.koin.compose.rememberKoinInject

@Composable
fun ServerNameInputField(
    modifier: Modifier = Modifier, viewModel: BroadcastViewModel = rememberKoinInject()
) {
    val nameInput by viewModel.serverName.collectAsState()
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = nameInput,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        onValueChange = viewModel::nameChanged,
        placeholder = { Text("Unknown", color = Colors.TEXT.copy(0.5f)) },
        label = { Text("Name", color = Colors.TEXT) },
        shape = RoundedCornerShape(20.dp),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrectEnabled = true,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            viewModel.saveName(nameInput)
            focusManager.clearFocus()
        }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Colors.TEXT,
            unfocusedTextColor = Colors.TEXT,
            focusedBorderColor = Colors.PRIMARY,
            unfocusedBorderColor = Colors.PRIMARY,
            cursorColor = Colors.ACCENT,
        )
    )
}