package com.github.picture2pc.desktop.data.imageprep

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Point

interface PicturePreparation {
    var originalBitmap: Bitmap
    var editedBitmap: StateFlow<Bitmap>
    var overlayBitmap: StateFlow<Bitmap>

    val ratio: Float
    val clicks: MutableList<Point>

    fun calculateRatio(displayPictureSize: IntSize)

    fun applyContrast()
    fun crop()
    fun copyToClipboard()

    fun reset(
        clearClicks: Boolean = true,
        clearOverlay: Boolean = true
    )

    fun setOriginalPicture(picture: Bitmap)
    fun addClick(offset: Offset)
}