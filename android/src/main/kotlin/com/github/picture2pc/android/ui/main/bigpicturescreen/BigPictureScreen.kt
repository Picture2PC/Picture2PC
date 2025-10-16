package com.github.picture2pc.android.ui.main.bigpicturescreen

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.bigpicturescreen.elements.BottomOfScreen
import com.github.picture2pc.android.ui.main.bigpicturescreen.elements.CurrentImageView
import com.github.picture2pc.android.ui.util.Settings
import com.github.picture2pc.android.ui.util.clampInRect
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.ui.Style
import kotlinx.coroutines.coroutineScope
import org.koin.compose.rememberKoinInject

@Composable
fun BigPictureScreen(
    galleryImage: Bitmap? = null,
    cameraViewModel: CameraViewModel = rememberKoinInject(),
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject(),
    isVertical: Boolean = true
) {
    val cameraImage = cameraViewModel.currentPicture.collectAsState().value
    val pictureCorners = cameraViewModel.pictureCorners.collectAsState().value
    val image = galleryImage ?: cameraImage
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var lastScale by remember { mutableFloatStateOf(1f) }
    var lastOffset by remember { mutableStateOf(Offset.Zero) }
    var size by remember { mutableStateOf(Rect(0f, 0f, 0f, 0f)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .pointerInput(Unit) {
                coroutineScope {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (lastScale * zoom).coerceIn(1f, Settings.MAX_ZOOM_FACTOR)
                        offset = clampInRect(size, lastOffset + pan)
                    }
                }
            }
            .onSizeChanged {
                size = Rect(
                    it.width.toFloat() / -2,
                    it.height.toFloat() / -2,
                    it.width.toFloat() / 2,
                    it.height.toFloat() / 2
                )
            }
    ) {

        CurrentImageView(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .clickable(onClick = screenSelectorViewModel::toCamera), image, pictureCorners
        )

        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(if (isVertical) 1f else 0.5f)
        ) {
            if (isVertical) {
                HorizontalDivider(
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(top = 10.dp, bottom = 10.dp),
                    thickness = 4.dp,
                    color = Style.Colors.PRIMARY
                )
            }
            BottomOfScreen()
        }
    }
    LaunchedEffect(scale, offset) {
        lastScale = scale
        lastOffset = offset
    }
    BackHandler {
        screenSelectorViewModel.toCamera()
    }
}