package com.github.picture2pc.android.ui.main.MainScreen.elements


import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.github.picture2pc.android.viewmodel.MainScreenViewModels
import org.koin.compose.rememberKoinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInput(modifier: Modifier){
    val viewModel: MainScreenViewModels.BroadcastViewModel = rememberKoinInject()
    val name = viewModel.name.collectAsState()

    OutlinedTextField(value = name.value, singleLine = true, onValueChange = {viewModel.nameChanged(it)}, placeholder = {Text("Justin")}, label = { Text("Name") }, modifier=modifier, keyboardOptions = KeyboardOptions(
        capitalization= KeyboardCapitalization.Sentences, autoCorrect = true, imeAction =  ImeAction.Done))
}