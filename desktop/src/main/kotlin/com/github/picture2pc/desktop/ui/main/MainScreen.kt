package com.github.picture2pc.desktop.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.ui.main.buttons.ReloadButton
import com.github.picture2pc.desktop.ui.main.imagemanupulation.ImageManipulationButtons
import com.github.picture2pc.desktop.ui.main.picturedisplay.Picture

@Composable
fun MainScreen() {
    Row {
        Column(Modifier
            .fillMaxHeight()
            .fillMaxWidth(.8f)
            .padding(10.dp)
        ) {
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) { Picture() }
        }
        Column(Modifier
            .fillMaxSize()
            .padding(10.dp)
        ) {
            Row(Modifier.fillMaxHeight(.3f).align(Alignment.Start)) { ReloadButton() }
            Row(Modifier.fillMaxHeight(.3f).align(Alignment.End)) { ImageManipulationButtons() }
        }
    }
}
