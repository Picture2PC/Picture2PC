package com.github.picture2pc.android.ui.main.camerascreen.elements

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
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
    image: Bitmap,
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()
) {
    val imageOffsetX = remember { mutableFloatStateOf(0f) }
    val imageOffsetY = remember { mutableFloatStateOf(0f) }

    Image(
        bitmap = image.asImageBitmap(),
        contentDescription = "Taken Picture",
        modifier = Modifier
            .getBaseModifier(image.width, image.height)
            .offset {
                IntOffset(
                    imageOffsetX.floatValue.roundToInt(),
                    imageOffsetY.floatValue.roundToInt()
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        imageOffsetX.floatValue = 0f
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

private fun Modifier.getBaseModifier(width: Int, height: Int): Modifier {
    return if (width > height) {
        width(200.dp)
    } else {
        height(200.dp)
    }
}