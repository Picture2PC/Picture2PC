package com.github.picture2pc.android.ui.main.bigpicturescreen.elements

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.data.edgedetection.DetectedBox

@Composable
fun CurrentImageView(modifier: Modifier, image: Bitmap?, pictureCorners: DetectedBox?) {
    var pictureBoxSize by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .onSizeChanged { pictureBoxSize = it }
    ) {
        if (image != null) {
            Image(
                image.asImageBitmap(),
                contentDescription = "Big Picture",

                )
        }
        if (image != null && pictureCorners != null) {
            Canvas(
                modifier = Modifier
            ) {
                val imageAspectRatio = image.width.toFloat() / image.height
                val boxAspectRatio = pictureBoxSize.width.toFloat() / pictureBoxSize.height

                val scaleFactor = if (boxAspectRatio > imageAspectRatio) {
                    pictureBoxSize.height.toFloat() / image.height
                } else {
                    pictureBoxSize.width.toFloat() / image.width
                }

                val scaledImageSize = Size(
                    width = image.width * scaleFactor,
                    height = image.height * scaleFactor
                )

                val topLeftOffset = Offset(
                    x = (pictureBoxSize.width - scaledImageSize.width) / 2f,
                    y = (pictureBoxSize.height - scaledImageSize.height) / 2f
                )
                withTransform({
                    translate(left = topLeftOffset.x, top = topLeftOffset.y)
                }) {
                    pictureCorners.pointsBox.onEach {
                        drawCircle(
                            color = Color.Green,
                            radius = 10f,
                            center = Offset(
                                (it.x * scaledImageSize.width).toFloat(),
                                (it.y * scaledImageSize.height).toFloat()
                            ),
                            style = Fill
                        )
                    }
                }
            }
        }
    }
}