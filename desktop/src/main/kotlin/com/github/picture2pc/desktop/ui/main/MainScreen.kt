package com.github.picture2pc.desktop.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.ui.main.buttons.ReloadButton
import com.github.picture2pc.desktop.ui.main.picturedisplay.Picture

@Composable
fun MainScreen() {
    Row(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(0.7f, true)
                .padding(10.dp)
                .background(Color.Blue)
                .fillMaxHeight()
                .defaultMinSize(500.dp, 500.dp)
        ) {
            Box() {
                Box() { }
                Box() { }
                Box(Modifier.align(Alignment.Center)) { Picture() }
            }
        }
        Column(Modifier.padding(10.dp)) {
            Row ( Modifier
                .weight(0.3f).background(Color.Yellow)
                .defaultMinSize(200.dp, 500.dp)
            ) {

            }
            Row( Modifier
                .weight(0.3f)
                .background(Color.Green)
                .defaultMinSize(200.dp, 500.dp)
            ) {

            }
            Row( Modifier
                .weight(0.3f, true)
                .background(Color.Red)
                .fillMaxHeight()
                .defaultMinSize(200.dp, 500.dp)
            ) {
                ReloadButton()
            }
        }
    }
}