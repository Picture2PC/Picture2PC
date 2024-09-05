package com.github.picture2pc.desktop.data.imageprep

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skia.Bitmap

interface PicturePreparation {
    var originalBitmap: Bitmap
    var editedBitmap: State<Bitmap>
    var overlayBitmap: State<Bitmap>

    var ratio: Float
    var bounds: Rect
    var displayPictureSize: IntSize

    val clicks: MutableList<Pair<Float, Float>>

    fun calculateRatio(displayPictureSize: IntSize)

    fun contrast()
    fun crop()
    fun copy()
    fun reset(
        resetEditedBitmap: Boolean = true,
        resetClicks: Boolean = true,
        resetOverlay: Boolean = true
    )

    fun setOriginalPicture(picture: Bitmap): Boolean
    fun updateEditedBitmap()

    fun drawCircle(pair: Pair<Float, Float>, filled: Boolean = false)
    fun drawPolygon(pairs: MutableList<Pair<Float, Float>>)
    fun redrawAllPoints()
}