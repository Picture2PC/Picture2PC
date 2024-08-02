package com.github.picture2pc.desktop.ui.main.imagemanupulation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.ui.main.buttonShape
import com.github.picture2pc.desktop.ui.main.imagemanupulation.elements.ManipulationButton
import com.github.picture2pc.desktop.viewmodel.pictureviewmodel.PictureViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ImageManipulationButtons(
    pictureViewModel: PictureViewModel = rememberKoinInject()
) {
    val spacerSize: Modifier = Modifier.size(5.dp)

    //TODO: Do the icon loading properly

    Column {
        Row {
            ManipulationButton(
                {},
                "icons/reset.svg",
                "reset"
            )

            Spacer(spacerSize)

            ManipulationButton(
                {pictureViewModel.adjustCurrentPictureIndex(false)},
                "icons/previousPicture.svg",
                "previousPicture"
            )

            Spacer(spacerSize)

            ManipulationButton(
                {pictureViewModel.adjustCurrentPictureIndex(true)},
                "icons/nextPicture.svg",
                "nextPicture"
            )

            Spacer(spacerSize)
        }

        Row { Spacer(modifier = Modifier.height(10.dp)) }

        Row {
            Button(onClick = {}, shape = buttonShape, modifier = Modifier.fillMaxWidth())
            { Text("Do All") }
        }

        Row {
            ManipulationButton(
                {},
                "icons/contrast.svg",
                "contrast"
            )

            Spacer(spacerSize)

            ManipulationButton(
                {pictureViewModel.addPictureToClipboard()},
                "icons/copy.svg",
                "copy"
            )

            Spacer(spacerSize)

            ManipulationButton(
                {},
                "icons/crop.svg",
                "crop"
            )
        }
    }
}