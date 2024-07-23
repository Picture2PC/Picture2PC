package com.github.picture2pc.desktop.ui.main.imagemanupulation

import QualitySelector
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.ui.main.shape

@Composable
fun ImageManipulationButtons() {
    Column {
        Row {
            Button(onClick = {}, shape = shape)
            { Icon(painterResource("icons/reset.svg"), "reset") }
            Spacer(Modifier.size(5.dp))
            Button(onClick = {}, shape = shape)
            { Icon(painterResource("icons/previousPicture.svg"), "previousPicture") }
            Spacer(Modifier.size(5.dp))
            Button(onClick = {}, shape = shape)
            { Icon(painterResource("icons/nextPicture.svg"), "nextPicture") }
            Spacer(Modifier.size(5.dp))
        }
        Row {
            Button(onClick = {}, shape = shape, modifier = Modifier.fillMaxWidth())
            { Text("Do All") }
        }
        Row {
            Button(onClick = {}, shape = shape)
            { Icon(painterResource("icons/contrast.svg"), "contrast") }
            Spacer(Modifier.size(5.dp))
            Button(onClick = {}, shape = shape)
            { Icon(painterResource("icons/copy.svg"), "copy") }
            Spacer(Modifier.size(5.dp))
            Button(onClick = {}, shape = shape)
            { Icon(painterResource("icons/crop.svg"), "copy") }
        }
    }
}