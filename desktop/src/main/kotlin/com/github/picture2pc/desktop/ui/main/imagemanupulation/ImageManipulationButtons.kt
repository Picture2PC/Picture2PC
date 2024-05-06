package com.github.picture2pc.desktop.ui.main.imagemanupulation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ImageManipulationButtons(){
    Row {
        DropdownMenu(expanded = true, onDismissRequest = {}){}
    }
    Row {
        Column { ResetButton() }
        Column { ImageSelectorButtons() }
    }
    Row {
        Button(onClick = { }) {
            Text("Do All")
        }
    }
    Row {
        Column { ContrastButton() }
        Column { CopyButton() }
        Column { CropButton() }
    }
}