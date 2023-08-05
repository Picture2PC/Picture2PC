package com.github.picture2pc.android.ui.main.MainScreen.elements


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInput(modifier: Modifier){
    val state = remember{ mutableStateOf("a")}

    TextField(value = state.value, onValueChange = {state.value = it })
}