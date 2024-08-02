package com.github.picture2pc.desktop.ui.main.imagemanupulation.elements

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ManipulationButton(
    onClick: () -> Unit,
    shape: RoundedCornerShape,
    resourcePath: String,
    name: String
) {
    Button(
        onClick = onClick,
        shape = shape,
    ) {
        Icon(painterResource(resourcePath), name)
    }
}