package com.github.picture2pc.android.ui.main.bigpicturescreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.bigpicturescreen.elements.BottomOfScreen
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun HorizontalBigPictureScreen(
    cameraViewModel: CameraViewModel = rememberKoinInject(),
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()
) {
    val image = cameraViewModel.takenImage.collectAsState(initial = null).value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(modifier = Modifier.align(Alignment.Center)) {
            if (image != null)
                Image(
                    image.asImageBitmap(),
                    contentDescription = "Big Picture",
                    modifier = Modifier
                        .clickable(onClick = screenSelectorViewModel::toCamera)
                        .clip(RoundedCornerShape(20.dp))
                )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(.5f)
        ) {
            BottomOfScreen()
        }
    }
    BackHandler {
        screenSelectorViewModel.toCamera()
    }
}
