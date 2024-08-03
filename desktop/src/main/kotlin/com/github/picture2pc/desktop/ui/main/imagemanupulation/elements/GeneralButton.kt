package com.github.picture2pc.desktop.ui.main.imagemanupulation.elements

import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Shapes

@Composable
fun ManipulationButton(
    onClick: () -> Unit,
    resourcePath: String,
    name: String
) {
    Button(
        onClick = onClick,
        shape = Shapes.BUTTON,
        colors = Colors.BUTTON
    ) {
        Icon(painterResource(resourcePath), name)
    }
}