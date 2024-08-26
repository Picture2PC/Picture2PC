package com.github.picture2pc.android.ui.main.camerascreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.camerascreen.elements.CameraPreview
import com.github.picture2pc.android.ui.main.camerascreen.elements.PictureButtons
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

    Box(modifier = modifier.padding(30.dp)) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .width(IntrinsicSize.Max)
                .align(Alignment.Center)
        ) {
            CameraPreview(cameraViewModel = cameraViewModel)
        }
        if (image != null) {
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = "Taken Picture",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .height(175.dp)
                    .clickable(onClick = screenSelectorViewModel::toBigPicture)
                    .clip(RoundedCornerShape(20.dp))
                    .border(3.dp, color = Colors.PRIMARY, shape = RoundedCornerShape(20.dp))
            )
        }
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
            PictureButtons()
        }
    }
}