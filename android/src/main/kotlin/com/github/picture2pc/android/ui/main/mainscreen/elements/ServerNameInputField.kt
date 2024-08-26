package com.github.picture2pc.android.ui.main.mainscreen.elements


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import com.github.picture2pc.common.ui.Colors
import org.koin.compose.rememberKoinInject

@Composable
fun ServerNameInputField(
    viewModel: BroadcastViewModel = rememberKoinInject()
) {
    val nameInput by viewModel.serverName.collectAsState()

    OutlinedTextField(
        value = nameInput,
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
        keyboardActions = KeyboardActions(onDone = { viewModel.saveName(nameInput) }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Colors.PRIMARY,
            unfocusedBorderColor = Colors.PRIMARY,
            cursorColor = Colors.ACCENT,
        )
    )
}