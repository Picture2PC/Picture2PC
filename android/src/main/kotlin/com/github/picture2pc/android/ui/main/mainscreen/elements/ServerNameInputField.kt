package com.github.picture2pc.android.ui.main.mainscreen.elements


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ServerNameInputField(
    modifier: Modifier = Modifier, broadcastViewModel: BroadcastViewModel = rememberKoinInject()
) {
    val nameInput by broadcastViewModel.serverName.collectAsState()

    OutlinedTextField(
        value = nameInput,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        onValueChange = broadcastViewModel::nameChanged,
        placeholder = { Text("Unknown") },
        label = { Text("Name") },
        shape = RoundedCornerShape(20.dp),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrectEnabled = true,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { broadcastViewModel.saveName(nameInput) })
    )
}