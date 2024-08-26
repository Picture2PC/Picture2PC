package com.github.picture2pc.android.ui.main.camerascreen.elements

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.ui.Colors
import org.koin.compose.rememberKoinInject
import kotlin.math.roundToInt

@Composable
fun DisplayImage(
    modifier: Modifier = Modifier,
    image: Bitmap,
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()
) {
    val imageOffsetX = remember { mutableFloatStateOf(0f) }
    val imageOffsetY = remember { mutableFloatStateOf(0f) }

    Image(
        bitmap = image.asImageBitmap(),
        contentDescription = "Taken Picture",
        modifier = Modifier
            .offset {
                IntOffset(
                    imageOffsetX.floatValue.roundToInt(),
                    imageOffsetY.floatValue.roundToInt()
                )
            }
            .height(200.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        // Snap to top start or top end
                        if (imageOffsetX.floatValue < size.width / 2) {
                            imageOffsetX.floatValue = 0f
                        } else {
                            imageOffsetX.floatValue = size.width + 30.dp.toPx()
                            Log.d("DisplayImage", size.width.toString())
                        }
                        imageOffsetY.floatValue = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    imageOffsetX.floatValue += dragAmount.x
                    imageOffsetY.floatValue += dragAmount.y
                }
            }
            .clickable(onClick = screenSelectorViewModel::toBigPicture)
            .clip(RoundedCornerShape(20.dp))
            .border(3.dp, color = Colors.PRIMARY, shape = RoundedCornerShape(20.dp))
            .alpha(0.8f)
    )
}