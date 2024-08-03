package com.github.picture2pc.desktop.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Data
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.common.ui.getIcon
import com.github.picture2pc.desktop.ui.main.imagemanupulation.ImageManipulationButtons
import com.github.picture2pc.desktop.ui.main.imagemanupulation.QualitySelector
import com.github.picture2pc.desktop.ui.main.picturedisplay.Picture
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun MainScreen(
    serversSectionViewModel: ServersSectionViewModel = rememberKoinInject(),
    //pictureEditorViewModel: PictureEditorViewModel = rememberKoinInject()
) {
    serversSectionViewModel.refreshServers()

    //TODO: Add tooltips to buttons

    Box( Modifier
        .background(Colors.BACKGROUND)
        .fillMaxSize()
    ) {
        Row( Modifier
            .fillMaxSize()
            .padding(10.dp)
        ) {
            Column( Modifier
                .fillMaxHeight()
                .width(246.dp)
                .background(Colors.SECONDARY, Shapes.WINDOW)
            ) {
                Column( Modifier
                    .padding(10.dp)
                ) {
                    Row {
                        Image(getIcon(Icons.Logo.STANDARD), "Logo", modifier = Modifier.width(75.dp))
                        Text("  " + Data.APP_NAME, style = TextStyles.HEADER, modifier = Modifier.align(Alignment.CenterVertically))
                    }
                    Spacer(Modifier.height(15.dp))
                    Row { QualitySelector() }
                    Row { ImageManipulationButtons() }
                    Row { Button(serversSectionViewModel::refreshServers) {
                        Text("Refresh Servers") }
                    }
                }
            }

            Spacer( Modifier.width(5.dp) )

            Column( Modifier
                .fillMaxSize()
                .border(2.dp, Colors.PRIMARY, Shapes.WINDOW)
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