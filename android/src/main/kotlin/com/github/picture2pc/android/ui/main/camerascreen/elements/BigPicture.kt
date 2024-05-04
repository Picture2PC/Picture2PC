package com.github.picture2pc.android.ui.main.camerascreen.elements

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun BigPicture(
    image: Bitmap,
    cameraViewModel: CameraViewModel = rememberKoinInject(),
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()
){
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            bitmap = image.asImageBitmap(),
            modifier = Modifier.align(Alignment.Center),
            contentDescription = "Big last picture"
        )
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = { cameraViewModel.sendImage(); screenSelectorViewModel.toMain() },
            shape = shape
        ) {
            Text(text = "Send")
        }
    }
}