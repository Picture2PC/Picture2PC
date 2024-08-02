package com.github.picture2pc.desktop.ui.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.ui.main.imagemanupulation.ImageManipulationButtons
import com.github.picture2pc.desktop.ui.main.imagemanupulation.QualitySelector
import com.github.picture2pc.desktop.ui.main.picturedisplay.Picture
import com.github.picture2pc.desktop.viewmodel.pictureviewmodel.PictureViewModel
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import org.koin.compose.rememberKoinInject

val windowShape = RoundedCornerShape(12.dp)
val buttonShape = RoundedCornerShape(8.dp)

@Preview
@Composable
fun MainScreen(
    pictureViewModel: PictureViewModel = rememberKoinInject(),
    serversSectionViewModel: ServersSectionViewModel = rememberKoinInject()
) {
    serversSectionViewModel.refreshServers()

    //TODO: Add tooltips to buttons

    Box(Modifier.background(Color.DarkGray).fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .width(246.dp)
                    .background(Color.LightGray, windowShape)
            ) {
                Box(Modifier.padding(10.dp)) {
                    Column {
                        Row { QualitySelector() }
                        Row { ImageManipulationButtons() }
                        Row { Button(onClick = serversSectionViewModel::refreshServers){ Text("Refresh Servers") } }
                        Row { Button(onClick = pictureViewModel::testAction){ Text("Test Button") } }
                    }
                }
            }

            Column { Spacer(Modifier.width(5.dp)) }

            Column(
                Modifier
                    .fillMaxSize()
                    .border(2.dp, Color.Black, windowShape)
            ) {
                Box(
                    Modifier
                        .padding(10.dp)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Picture() }
            }
        }
    }
}