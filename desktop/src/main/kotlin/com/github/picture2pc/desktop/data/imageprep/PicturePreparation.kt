package com.github.picture2pc.desktop.data.imageprep

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skia.Bitmap

interface PicturePreparation {
    var originalBitmap: Bitmap
    var editedBitmap: State<Bitmap>

    var ratio: Float
    var bounds: Rect
    var displayPictureSize: IntSize

    fun calculateRatio(displayPictureSize: IntSize)

    fun contrast()
    fun crop(clicks: List<Offset>)
    fun copy()
    fun resetEditedBitmap()

    fun setOriginalPicture(picture: Bitmap)
    fun updateEditedBitmap()
}