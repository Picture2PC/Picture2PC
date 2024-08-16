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
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Data
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.common.ui.getIcon
import com.github.picture2pc.desktop.ui.main.imagemanupulation.ImageManipulationButtons
import com.github.picture2pc.desktop.ui.main.imagemanupulation.QualitySelector
import com.github.picture2pc.desktop.ui.main.picturedisplay.Picture
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun MainScreen(
    serversSectionViewModel: ServersSectionViewModel = rememberKoinInject(),
    pictureDisplayViewModel: PictureDisplayViewModel = rememberKoinInject()
) {
    serversSectionViewModel.refreshServers()

    //TODO: Add tooltips to buttons

    Box(
        Modifier
            .background(Colors.BACKGROUND)
            .fillMaxSize()
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(Spacers.NORMAL)
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .width(246.dp)
                    .background(Colors.SECONDARY, Shapes.WINDOW)
            ) {
                Column(Modifier.padding(Spacers.NORMAL)) { Row {
                    Image(
                        getIcon(Icons.Logo.STANDARD),
                        "Logo",
                        Modifier.width(75.dp)
                    )
                    Spacer(Modifier.width(Spacers.LARGE))

                    Text(
                        Data.APP_NAME,
                        Modifier.align(Alignment.CenterVertically),
                        style = TextStyles.HEADER
                    ) }
                    Spacer(Modifier.height(Spacers.LARGE))

                    Row { QualitySelector() }
                    Spacer(Modifier.height(Spacers.SMALL))

                    Row { ImageManipulationButtons() }

                    Button(serversSectionViewModel::refreshServers) {
                        Text("Refresh Servers")
                    }
                    Button({ pictureDisplayViewModel.loadTestImage() }) {
                        Text("Load Test Image")
                    }
                }
            }
            Spacer(Modifier.width(Spacers.NORMAL))

            Column(
                Modifier
                    .fillMaxSize()
                    .border(Borders.BORDER_STANDARD, Colors.PRIMARY, Shapes.WINDOW)
            ) {
                Box(
                    Modifier
                        .padding(Spacers.NORMAL)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Picture() }
            }
        }
    }
}