package com.github.picture2pc.desktop.data.imageprep

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Point

interface PicturePreparation {
    var originalBitmap: Bitmap

    var editedBitmap: State<Bitmap>
    var overlayBitmap: State<Bitmap>
    var dragOverlayBitmap: State<Bitmap>

    var ratio: Float
    val clicks: MutableList<Point>

    fun calculateRatio(displayPictureSize: IntSize)

    fun applyContrast()
    fun crop()
    fun copyToClipboard()

    fun reset(
        resetEditedBitmap: Boolean = true,
        clearClicks: Boolean = true,
        clearOverlay: Boolean = true,
        clearDragOverlay: Boolean = true
    )

    fun setDragStart(dragStart: Offset)
    fun resetDrag()

    fun setOriginalPicture(picture: Bitmap)
    fun addClick(offset: Offset)
    fun handleDrag(change: PointerInputChange, dragAmount: Offset)
}