package com.github.picture2pc.desktop.ui.main.imagemanupulation.elements

import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.painterResource
import com.github.picture2pc.desktop.ui.main.buttonShape

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ManipulationButton(
    onClick: () -> Unit,
    resourcePath: String,
    name: String
) {
    Button(
        onClick = onClick,
        shape = buttonShape,
    ) {
        Icon(painterResource(resourcePath), name)
    }
}