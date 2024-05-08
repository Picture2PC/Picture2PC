package com.github.picture2pc.desktop.ui.main.imagemanupulation

import QualitySelector
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

val shape = RoundedCornerShape(20)
@Composable
fun ImageManipulationButtons(){
    Column {
        Row {
            QualitySelector()
        }
        Row {
                Button( onClick = {}, shape = shape )
                { Icon(painterResource("icons/reset.svg"), "reset") }
                Button( onClick = {}, shape = shape )
                { Icon(painterResource("icons/previousPicture.svg"), "previousPicture") }
                Button( onClick = {}, shape = shape )
                { Icon(painterResource("icons/nextPicture.svg"), "nextPicture") }
        }
        Row {
            Button(onClick = { }, modifier = Modifier.fillMaxWidth(), shape = shape )
            { Text("Do All") }
        }
        Row {
            Column {
                Button( onClick = {}, shape = shape )
                { Icon(painterResource("icons/contrast.svg"), "contrast") }
            }
            Column {
                Button( onClick = {}, shape = shape )
                { Icon(painterResource("icons/copy.svg"), "copy") }
            }
            Column {
                Button( onClick = {}, shape = shape )
                { Icon(painterResource("icons/crop.svg"), "copy") }
            }
        }
    }
}