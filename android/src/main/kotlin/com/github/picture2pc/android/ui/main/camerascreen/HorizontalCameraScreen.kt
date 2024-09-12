package com.github.picture2pc.android.ui.main.camerascreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.camerascreen.elements.BottomOfScreen
import com.github.picture2pc.android.ui.main.camerascreen.elements.CameraPreview
import com.github.picture2pc.android.ui.main.camerascreen.elements.DisplayImage
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun HorizontalCameraScreen(
    modifier: Modifier = Modifier,
    cameraViewModel: CameraViewModel = rememberKoinInject(),
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()
) {
    val image = cameraViewModel.takenImage.collectAsState(initial = null).value

    Box(
        Modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {
        Row(
            Modifier
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Max)
                .align(Alignment.Center)
        ) { CameraPreview(cameraViewModel) }
        Row { if (image != null) DisplayImage(image = image) }
        Row(
            Modifier
                .fillMaxWidth(.5f)
                .align(Alignment.BottomCenter)
        ) { BottomOfScreen() }
    }
    BackHandler {
        screenSelectorViewModel.toMain()
    }
}