package com.github.picture2pc.desktop.ui.main.picture

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.picture2pc.desktop.viewmodel.pictureviewmodel.PictureViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Picture(
    modifier: Modifier = Modifier,
    pictureViewModel: PictureViewModel = rememberKoinInject()
) {
    val picture by pictureViewModel.pictures.collectAsState(initial = null)
    Surface(
        color = Color.Green,
        modifier = Modifier.then(modifier)
    ) {
        Box {
            picture?.let {
                Image(
                    bitmap = it.toComposeImageBitmap(),
                    contentDescription = "Picture"
                )
            }
        }
    }
}
