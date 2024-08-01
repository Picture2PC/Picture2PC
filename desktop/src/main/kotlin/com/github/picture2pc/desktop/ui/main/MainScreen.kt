package com.github.picture2pc.desktop.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.ui.main.connectivitybuttons.ReloadButton
import com.github.picture2pc.desktop.ui.main.imagemanupulation.ImageManipulationButtons
import com.github.picture2pc.desktop.ui.main.imagemanupulation.QualitySelector
import com.github.picture2pc.desktop.ui.main.picturedisplay.Picture
import com.github.picture2pc.desktop.viewmodel.pictureviewmodel.PictureViewModel
import org.koin.compose.rememberKoinInject

val shape = RoundedCornerShape(20)

@Composable
fun MainScreen(
    pictureViewModel: PictureViewModel = rememberKoinInject()
) {
    Box(Modifier.fillMaxSize()){
        Column {
            Picture()
        }
        Column(Modifier
            .fillMaxHeight()
            .background(Color.LightGray)
            .align(Alignment.TopEnd)
            .padding(10.dp)
            .width(228.dp)
        ) {
            Row { QualitySelector() }
            Row { ImageManipulationButtons() }
            Row { ReloadButton() }
            Row { Button(onClick = pictureViewModel::testAction){ Text("Test Button") } }
        }
    }
}