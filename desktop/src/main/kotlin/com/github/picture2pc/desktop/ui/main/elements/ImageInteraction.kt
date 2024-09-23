package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Heights
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.ui.constants.Descriptions
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

fun getCurrentIndex(currentIndex: Int, totalPictures: Int): String {
    if (totalPictures == 0) return "0 / 0"
    return "${currentIndex + 1} / $totalPictures"
}

@Composable
fun ImageInteractionButtons(
    pDVM: PictureDisplayViewModel = rememberKoinInject()
) {
    val spacerSize: Modifier = Modifier.size(Spacers.SMALL)
    val currentIndex = pDVM.selectedPictureIndex.collectAsState().value
    val totalPictures = pDVM.totalPictures.collectAsState().value

    Column {
        Row {
            TooltipIconButton(
                description = Descriptions.RESET,
                icon = Icons.Desktop.RESET,
                buttonModifier = Modifier.width(75.dp)
            ) {
                pDVM.pP.resetEditedBitmap()
                pDVM.movementHandler.clear()
            }
            Spacer(spacerSize)

            Column(Modifier.background(Colors.ACCENT, Shapes.BUTTON)) {
                Row(Modifier.background(Colors.PRIMARY, Shapes.BUTTON).fillMaxWidth()) {
                    Spacer(Modifier.width(Spacers.LARGE))

                    TooltipIconButton(
                        description = Descriptions.PREVIOUS_PICTURE,
                        icon = Icons.Desktop.PREVIOUS_PICTURE
                    ) { pDVM.adjustCurrentPictureIndex(-1) }
                    Spacer(Modifier.weight(1f))

                    TooltipIconButton(
                        description = Descriptions.NEXT_PICTURE,
                        icon = Icons.Desktop.NEXT_PICTURE
                    ) { pDVM.adjustCurrentPictureIndex(1) }
                    Spacer(Modifier.width(Spacers.LARGE))
                }
                Row(Modifier.align(Alignment.CenterHorizontally).padding(Spacers.SMALL)) {
                    Text(
                        text = getCurrentIndex(currentIndex, totalPictures),
                        style = TextStyles.SMALL
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(Spacers.NORMAL))

        Column(Modifier.background(Colors.PRIMARY, Shapes.BUTTON)) {
            Row {
                Button(
                    onClick = {
                        if (pDVM.movementHandler.clicks.value.size != 4) return@Button
                        pDVM.pP.crop(pDVM.movementHandler.clicks.value)
                        pDVM.pP.contrast()
                        pDVM.pP.copy()
                    },
                    Modifier.fillMaxWidth().height(Heights.BUTTON),
                    shape = Shapes.BUTTON,
                    colors = Colors.BUTTON_SECONDARY,
                ) { Text("Do All", style = TextStyles.NORMAL) }
            }

            Divider(
                Modifier.padding(horizontal = Spacers.NORMAL),
                Heights.DIVIDER,
                Colors.SECONDARY
            )

            Row {
                Spacer(Modifier.weight(1f))
                TooltipIconButton(
                    description = Descriptions.CONTRAST,
                    icon = Icons.Desktop.CONTRAST
                ) { pDVM.pP.contrast() }
                Spacer(Modifier.weight(1f))

                TooltipIconButton(
                    description = Descriptions.COPY,
                    icon = Icons.Desktop.COPY
                ) { pDVM.pP.copy() }
                Spacer(Modifier.weight(1f))

                TooltipIconButton(
                    description = Descriptions.CROP,
                    icon = Icons.Desktop.CROP
                ) {
                    pDVM.pP.crop(pDVM.movementHandler.clicks.value)
                }
                Spacer(Modifier.weight(1f))
            }
        }
    }
}