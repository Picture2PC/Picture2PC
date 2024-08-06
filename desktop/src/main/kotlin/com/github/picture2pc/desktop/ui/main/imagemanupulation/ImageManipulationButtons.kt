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
import com.github.picture2pc.desktop.ui.main.imagemanupulation.elements.ManipulationButton
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

fun getCurrentIndex(currentIndex: Int, totalPictures: Int): String {
    if (totalPictures == 0) return "0 / 0"
    return "${currentIndex + 1} / $totalPictures"
}

@Composable
fun ImageManipulationButtons(
    pictureDisplayViewModel: PictureDisplayViewModel = rememberKoinInject()
) {
    val spacerSize: Modifier = Modifier.size(Spacers.SMALL)
    val currentIndex = pictureDisplayViewModel.currentPictureIndex.collectAsState().value
    val totalPictures = pictureDisplayViewModel.totalPictures.collectAsState().value

    Column {
        Row {
            ManipulationButton(
                {pictureDisplayViewModel.currentPictureEditor?.resetCanvas(true)},
                Icons.Desktop.RESET,
                "reset"
            )
            Spacer(spacerSize)

            Column(Modifier.background(Colors.ACCENT, Shapes.BUTTON)) {
                Row(Modifier.background(Colors.PRIMARY, Shapes.BUTTON)) {
                    ManipulationButton(
                        { pictureDisplayViewModel.adjustCurrentPictureIndex(false) },
                        Icons.Desktop.PREVIOUS_PICTURE,
                        "previousPicture"
                    )
                    Spacer(spacerSize)

                    ManipulationButton(
                        { pictureDisplayViewModel.adjustCurrentPictureIndex(true) },
                        Icons.Desktop.NEXT_PICTURE,
                        "nextPicture"
                    )
                }
                Row(Modifier.align(Alignment.End).padding(Spacers.SMALL)) {
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
                    onClick = {},
                    shape = Shapes.BUTTON,
                    colors = Colors.BUTTON_SECONDARY,
                    modifier = Modifier.fillMaxWidth().height(Heights.BUTTON)
                ) {
                    Text("Do All", style = TextStyles.NORMAL)
                }
            }

            Divider(Modifier.padding(horizontal = Spacers.NORMAL), Heights.DIVIDER, Colors.SECONDARY)

            Row {
                ManipulationButton(
                    { pictureDisplayViewModel.currentPictureEditor?.contrast() },
                    Icons.Desktop.CONTRAST,
                    "contrast"
                )
                Spacer(spacerSize)

                ManipulationButton(
                    { pictureDisplayViewModel.addPictureToClipboard() },
                    Icons.Desktop.COPY,
                    "copy"
                )
                Spacer(spacerSize)

                ManipulationButton(
                    { pictureDisplayViewModel.currentPictureEditor?.crop() },
                    Icons.Desktop.CROP,
                    "crop"
                )
            }
        }
    }
}