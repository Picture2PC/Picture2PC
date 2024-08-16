package com.github.picture2pc.desktop.data.imageprep

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Point

interface PicturePreparation {
    var originalBitmap: Bitmap
    var editedBitmap: State<Bitmap>
    var overlayBitmap: State<Bitmap>
    var dragOverlayBitmap: State<Bitmap>
    var zoomedBitmap: MutableState<Bitmap>

    var ratio: Float
    val clicks: MutableList<Point>

    var currentDragPoint: MutableState<Point>
    var dragActive: MutableState<Boolean>

    fun calculateRatio(displayPictureSize: IntSize)
    fun calculateOffset(): Pair<Dp, Dp>

    fun applyContrast()
    fun crop()
    fun copyToClipboard()

    fun reset(
        resetEditedBitmap: Boolean = true,
        resetClicks: Boolean = true,
        resetOverlay: Boolean = true,
        resetDragOverlay: Boolean = true
    )

    fun setDragStart(dragStart: Offset)
    fun resetDrag()

    fun setOriginalPicture(picture: Bitmap)
    fun handleClick(offset: Offset)
    fun handleDrag(change: PointerInputChange, dragAmount: Offset)
}