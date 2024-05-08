package com.github.picture2pc.desktop.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.ui.main.connectivitybuttons.ReloadButton
import com.github.picture2pc.desktop.ui.main.imagemanupulation.ImageManipulationButtons
import com.github.picture2pc.desktop.ui.main.picturedisplay.Picture

@Composable
fun MainScreen() {
    Row {
        Column(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.8f)
                .padding(10.dp)
                .background(Color.Green)
        ) {
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) { Picture() }
        }
        Column(
            Modifier
                .wrapContentSize()
                .padding(10.dp)
                .background(Color.Blue)
        ) {
            Box(Modifier.fillMaxHeight(.3f).background(Color.Red)) { ImageManipulationButtons() }
            Box(Modifier.fillMaxHeight(.3f).background(Color.Yellow)) { ReloadButton() }
        }
    }
}