package com.github.picture2pc.android.ui.main.camerascreen.elements

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.R
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

val shape = RoundedCornerShape(5.dp)

@Composable
fun PictureButtons(
    cameraViewModel: CameraViewModel = rememberKoinInject(),
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.weight(.5f, true), horizontalAlignment = Alignment.Start) {
            IconButton( onClick = screenSelectorViewModel::toMain ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
            }
        }
        Column {
            IconButton(onClick = cameraViewModel::switchFlashMode) {
                Icon(
                    painterResource(R.drawable.app_icon_standard),
                    "Flash Mode On"
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Button(
                onClick = cameraViewModel::takeImage,
                shape = shape
            ) {
                Text(text = "Take Picture")
            }
        }
    }
}