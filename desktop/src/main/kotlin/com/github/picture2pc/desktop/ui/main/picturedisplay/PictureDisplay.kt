package com.github.picture2pc.desktop.ui.main.picturedisplay

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.picture2pc.desktop.viewmodel.pictureviewmodel.PictureViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Picture(
    pictureViewModel: PictureViewModel = rememberKoinInject()
) {
    val picture = pictureViewModel.currentPicture.collectAsState().value
    picture?.let {
        Image(
            bitmap = it.toComposeImageBitmap(),
            contentDescription = "Picture"
        )
    }
}