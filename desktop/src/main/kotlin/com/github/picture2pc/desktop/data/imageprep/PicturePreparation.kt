package com.github.picture2pc.desktop.data.imageprep

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.jetbrains.skia.Bitmap

interface PicturePreparation {
    var originalBitmap: Bitmap
    var editedBitmap: State<Bitmap>

    var ratio: Float

    fun calculateRatio(displayPictureSize: Size)

    fun contrast()
    fun crop(clicks: List<Offset>, displayPictureSize: Size)
    fun copy()

    fun setOriginalPicture(picture: Bitmap)
}