package com.github.picture2pc.desktop.ui.main.imagemanupulation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Heights
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.ui.constants.Descriptions
import com.github.picture2pc.desktop.ui.main.imagemanupulation.elements.InteractionIconButton
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
            InteractionIconButton(
                { pDVM.pP.reset() },
                Icons.Desktop.RESET,
                Descriptions.RESET
            )
            Spacer(spacerSize)

            Column(Modifier.background(Colors.ACCENT, Shapes.BUTTON)) {
                Row(Modifier.background(Colors.PRIMARY, Shapes.BUTTON)) {
                    InteractionIconButton(
                        { pDVM.adjustCurrentPictureIndex(-1) },
                        Icons.Desktop.PREVIOUS_PICTURE,
                        Descriptions.PREVIOUS_PICTURE
                    )
                    Spacer(spacerSize)

                    InteractionIconButton(
                        { pDVM.adjustCurrentPictureIndex(1) },
                        Icons.Desktop.NEXT_PICTURE,
                        Descriptions.NEXT_PICTURE
                    )
                }
                Row(Modifier.align(Alignment.CenterHorizontally).padding(Spacers.SMALL)) {
                    Text(
                        getCurrentIndex(currentIndex, totalPictures),
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
                        if (pDVM.pP.clicks.size != 4) return@Button
                        pDVM.pP.crop()
                        pDVM.pP.contrast()
                        pDVM.pP.copy()
                    },
                    shape = Shapes.BUTTON,
                    colors = Colors.BUTTON_SECONDARY,
                    modifier = Modifier.fillMaxWidth().height(Heights.BUTTON)
                ) {
                    Text("Do All", style = TextStyles.NORMAL)
                }
            }

            Divider(
                Modifier.padding(horizontal = Spacers.NORMAL),
                Heights.DIVIDER,
                Colors.SECONDARY
            )

            Row {
                InteractionIconButton(
                    { pDVM.pP.contrast() },
                    Icons.Desktop.CONTRAST,
                    Descriptions.CONTRAST
                )
                Spacer(spacerSize)

                InteractionIconButton(
                    { pDVM.pP.copy() },
                    Icons.Desktop.COPY,
                    Descriptions.COPY
                )
                Spacer(spacerSize)

                InteractionIconButton(
                    { pDVM.pP.crop() },
                    Icons.Desktop.CROP,
                    Descriptions.CROP
                )
            }
        }
    }
}