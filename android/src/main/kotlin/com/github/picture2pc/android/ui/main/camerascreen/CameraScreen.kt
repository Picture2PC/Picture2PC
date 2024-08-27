package com.github.picture2pc.android.ui.main.camerascreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.camerascreen.elements.BottomOfScreen
import com.github.picture2pc.android.ui.main.camerascreen.elements.CameraPreview
import com.github.picture2pc.android.ui.main.camerascreen.elements.DisplayImage
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.ui.Colors
import org.koin.compose.rememberKoinInject

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    cameraViewModel: CameraViewModel = rememberKoinInject(),
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()
) {
    val image = cameraViewModel.takenImage.collectAsState(initial = null).value

    Box(modifier = modifier.padding(20.dp)) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .width(IntrinsicSize.Max)
                .align(Alignment.Center)
        ) {
            CameraPreview(cameraViewModel = cameraViewModel)
        }
        if (image != null)
            DisplayImage(image = image)
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(top = 10.dp, bottom = 10.dp),
                thickness = 4.dp,
                color = Colors.PRIMARY
            )
            BottomOfScreen()
        }
    }
    BackHandler {
        screenSelectorViewModel.toMain()
    }
}