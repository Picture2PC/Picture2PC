package com.github.picture2pc.desktop.ui.main.imagemanupulation.elements

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Heights
import com.github.picture2pc.common.ui.Shapes

@Composable
fun ManipulationButton(
    onClick: () -> Unit,
    resourcePath: String,
    name: String
) {
    Button(
        onClick,
        Modifier.height(Heights.BUTTON),
        shape = Shapes.BUTTON,
        colors = Colors.BUTTON_PRIMARY
    ) {
        Icon(painterResource(resourcePath), name, tint = Colors.TEXT)
    }
}