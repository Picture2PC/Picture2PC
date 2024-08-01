package com.github.picture2pc.desktop.ui.main.imagemanupulation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val shape = RoundedCornerShape(20)

@Composable
fun QualitySelector() {
    val options = listOf("Low", "Medium", "High")
    val clicked = remember { mutableStateOf(false) }
    val qualityName = remember { mutableStateOf(options.first()) }

    Row(verticalAlignment = Alignment.CenterVertically){
        Text("Output Quality")
        Spacer(Modifier.size(5.dp))
        Button(onClick = { clicked.value = true }, shape = shape, modifier = Modifier.fillMaxWidth()) {
            Text(qualityName.value)
        }
    }
    DropdownMenu(
        expanded = clicked.value,
        onDismissRequest = { clicked.value = false }
    ) {
        options.forEach { label ->
            DropdownMenuItem(onClick = {
                clicked.value = false
                qualityName.value = label
            }, text = { Text(label) })
        }
    }
}