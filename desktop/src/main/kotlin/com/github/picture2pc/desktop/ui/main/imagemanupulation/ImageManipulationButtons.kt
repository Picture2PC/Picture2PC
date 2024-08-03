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
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.ui.main.imagemanupulation.elements.ManipulationButton
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ImageManipulationButtons(
    pictureDisplayViewModel: PictureDisplayViewModel = rememberKoinInject()
) {
    val spacerSize: Modifier = Modifier.size(5.dp)

    Column {
        Row {
            ManipulationButton(
                {},
                Icons.Desktop.RESET,
                "reset"
            )
            Spacer(spacerSize)

            ManipulationButton(
                {pictureDisplayViewModel.adjustCurrentPictureIndex(false)},
                Icons.Desktop.PREVIOUS_PICTURE,
                "previousPicture"
            )
            Spacer(spacerSize)

            ManipulationButton(
                {pictureDisplayViewModel.adjustCurrentPictureIndex(true)},
                Icons.Desktop.NEXT_PICTURE,
                "nextPicture"
            )
            Spacer(spacerSize)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            Button(onClick = {}, shape = Shapes.BUTTON, colors = Colors.BUTTON ,modifier = Modifier.fillMaxWidth())
            { Text("Do All", style = TextStyles.NORMAL) }
        }

        Row {
            ManipulationButton(
                {},
                Icons.Desktop.CONTRAST,
                "contrast"
            )
            Spacer(spacerSize)

            ManipulationButton(
                {pictureDisplayViewModel.addPictureToClipboard()},
                Icons.Desktop.COPY,
                "copy"
            )
            Spacer(spacerSize)

            ManipulationButton(
                {},
                Icons.Desktop.CROP,
                "crop"
            )
        }
    }
}