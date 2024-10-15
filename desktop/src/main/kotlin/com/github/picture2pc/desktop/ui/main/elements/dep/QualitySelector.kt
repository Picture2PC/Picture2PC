package com.github.picture2pc.desktop.ui.main.elements.dep

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Heights
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.TextStyles

@Composable
fun QualitySelector() {
    val qualityOptions = listOf("Low", "Medium", "High")
    var selectedQuality by remember { mutableStateOf(qualityOptions.first()) }
    var isClicked by remember { mutableStateOf(false) }
    var buttonWidthPx by remember { mutableStateOf(0) }

    Box(Modifier.background(Colors.ACCENT, Shapes.BUTTON)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Output Quality",
                Modifier.padding(start = Spacers.MEDIUM, end = Spacers.MEDIUM),
                style = TextStyles.NORMAL,
            )

            Box {
                Button(
                    onClick = { isClicked = !isClicked },
                    shape = Shapes.BUTTON,
                    colors = Colors.BUTTON_PRIMARY,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Heights.BUTTON)
                        .onGloballyPositioned { coordinates ->
                            buttonWidthPx = coordinates.size.width
                        }
                ) { Text(selectedQuality, style = TextStyles.NORMAL) }

                DropdownMenu(
                    expanded = isClicked,
                    onDismissRequest = { },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { buttonWidthPx.toDp() })
                        .background(Colors.BACKGROUND)
                        .border(Borders.BORDER_STANDARD, Colors.ACCENT)
                ) {
                    qualityOptions.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                isClicked = false
                                selectedQuality = option
                            },
                            text = { Text(option, style = TextStyles.SMALL) },
                            modifier = Modifier
                                .height(25.dp)
                                .background(Colors.BACKGROUND)
                        )
                    }
                }
            }
        }
    }
}