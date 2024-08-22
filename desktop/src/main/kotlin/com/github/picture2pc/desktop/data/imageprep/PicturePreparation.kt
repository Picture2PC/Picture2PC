package com.github.picture2pc.desktop.data.imageprep

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Point

interface PicturePreparation {
    var originalBitmap: Bitmap
    var editedBitmap: State<Bitmap>
    var overlayBitmap: State<Bitmap>
    var zoomedBitmap: MutableState<Bitmap>

    var ratio: Float
    var editedBitmapBound: Rect

    val clicks: MutableList<Point>
    var currentDragPoint: MutableState<Point>

    fun calculateRatio(displayPictureSize: IntSize)
    fun calculateOffset(): Pair<Dp, Dp>

    fun contrast()
    fun crop()
    fun copy()
    fun reset(
        resetEditedBitmap: Boolean = true,
        resetClicks: Boolean = true,
        resetOverlay: Boolean = true,
        resetDragOverlay: Boolean = true
    )

    fun setOriginalPicture(picture: Bitmap)
    fun setDisplayedZoomedBitmap(point: Point)
    fun updateEditedBitmap()

    //fun getCorners(): MutableList<Array<org.opencv.core.Point>>?

    fun drawCircle(point: Point, filled: Boolean = false)
    fun drawPolygon(points: List<Point>, paint: Paint)
}