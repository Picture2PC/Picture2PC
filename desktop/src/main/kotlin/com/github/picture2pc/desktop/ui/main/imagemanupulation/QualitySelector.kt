package com.github.picture2pc.desktop.ui.main.imagemanupulation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.ui.main.buttonShape

@Composable
fun QualitySelector() {
    val qualityOptions = listOf("Low", "Medium", "High")
    var selectedQuality by remember { mutableStateOf(qualityOptions.first()) }
    var isClicked by remember { mutableStateOf(false) }
    var buttonWidthPx by remember { mutableStateOf(0) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            "Output Quality",
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.size(5.dp))

        Box {
            Button(
                onClick = { isClicked = !isClicked },
                shape = buttonShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        buttonWidthPx = coordinates.size.width
                    }
            ) {
                Text(selectedQuality)
            }

            DropdownMenu(
                expanded = isClicked,
                onDismissRequest = {},
                modifier = Modifier
                    .width(with(LocalDensity.current) { buttonWidthPx.toDp() })
            ) {
                qualityOptions.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            isClicked = false
                            selectedQuality = option },
                        text = { Text(option) },
                        modifier = Modifier.height(25.dp)
                    )
                }
            }
        }
    }
}