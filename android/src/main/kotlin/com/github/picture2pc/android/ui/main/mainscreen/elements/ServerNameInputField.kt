package com.github.picture2pc.android.ui.main.mainscreen.elements


import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import org.koin.compose.rememberKoinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerNameInputField(modifier: Modifier, viewModel: BroadcastViewModel = rememberKoinInject()) {

    val nameInput by viewModel.serverName.collectAsState()

    OutlinedTextField(
        value = nameInput,
        singleLine = true,
        onValueChange = viewModel::nameChanged,
        placeholder = { Text("Unknown") },
        label = { Text("Name") },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrect = true,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { viewModel.saveName(nameInput) })
    )
}