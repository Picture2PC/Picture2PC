package com.github.picture2pc.desktop.ui.main.imagemanupulation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.ui.main.imagemanupulation.elements.ManipulationButton
import com.github.picture2pc.desktop.ui.main.shape
import com.github.picture2pc.desktop.viewmodel.pictureviewmodel.PictureViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun spacer() {
    Spacer(Modifier.size(5.dp))
}

@Composable
fun ImageManipulationButtons(
    pictureViewModel: PictureViewModel = rememberKoinInject()
) {
    Column {
        Row {
            ManipulationButton({}, shape, "icons/reset.svg", "reset")
            spacer()
            ManipulationButton(
                {pictureViewModel.adjustCurrentPictureIndex(false)},
                shape, "icons/previousPicture.svg", "previousPicture")
            spacer()
            ManipulationButton(
                {pictureViewModel.adjustCurrentPictureIndex(true)},
                shape, "icons/nextPicture.svg", "nextPicture")
            spacer()
        }
        Row {
            Button(onClick = {}, shape = shape, modifier = Modifier.fillMaxWidth())
            { Text("Do All") }
        }
        Row {
            ManipulationButton({}, shape, "icons/contrast.svg", "contrast")
            spacer()
            ManipulationButton(
                {pictureViewModel.addPictureToClipboard()},
                shape, "icons/copy.svg", "copy"
            )
            spacer()
            ManipulationButton({}, shape, "icons/crop.svg", "crop")
        }
    }
}