package com.github.picture2pc.desktop.data.imageprep.impl

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.github.picture2pc.desktop.data.addToClipboard
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.data.imageprep.constants.Paints
import com.github.picture2pc.desktop.data.imageprep.constants.UnstratifiedValues
import com.github.picture2pc.desktop.extention.toBitmap
import com.github.picture2pc.desktop.extention.toImage
import com.github.picture2pc.desktop.extention.toMat
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorFilter
import org.jetbrains.skia.ColorMatrix
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Path
import org.jetbrains.skiko.toBufferedImage
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
import androidx.compose.ui.geometry.Rect as MathRect

class PicturePreparationImpl : PicturePreparation {
    //Bitmaps for the original, edited, overlay and drag overlay images
    override var originalBitmap: Bitmap = Bitmap()

    private var _editedBitmap: MutableState<Bitmap> = mutableStateOf(Bitmap())
    override var editedBitmap: State<Bitmap> = _editedBitmap

    private var _overlayBitmap: MutableState<Bitmap> = mutableStateOf(clearBitmap())
    override var overlayBitmap: State<Bitmap> = _overlayBitmap

    //Important other variables
    override var ratio: Float = 1f
    override var bounds = MathRect(Offset(0f, 0f), 0f)
    override var displayPictureSize = IntSize(0, 0)

    override val clicks: MutableList<Pair<Float, Float>> = mutableListOf()

    override fun contrast() {
        if (editedBitmap.value.isEmpty) return
        val contrast = 1.6f
        val cM = ColorMatrix(
            contrast, 0f, 0f, 0f, 0f,
            0f, contrast, 0f, 0f, 0f,
            0f, 0f, contrast, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        val paint = Paint().apply { colorFilter = ColorFilter.makeMatrix(cM) }

        val bitmap = clearBitmap()
        Canvas(bitmap).drawImage(editedBitmap.value.toImage(), 0f, 0f, paint).close()
        _editedBitmap.value = bitmap
        updateEditedBitmap()
    }

    override fun crop() {
        if (clicks.size != 4) return
        if (editedBitmap.value.isEmpty) return

        val tl = clicks[0] // Top Left
        val tr = clicks[1] // Top Right
        val br = clicks[2] // Bottom Right
        val bl = clicks[3] // Bottom Left

        val widthA = sqrt((tr.first - tl.first).pow(2) + (tr.second - tl.second).pow(2))
        val widthB = sqrt((br.first - bl.first).pow(2) + (br.second - bl.second).pow(2))
        val maxWidth = max(widthA, widthB).toDouble()

        val heightA = sqrt((tr.first - br.first).pow(2) + (tr.second - br.second).pow(2))
        val heightB = sqrt((tl.first - bl.first).pow(2) + (tl.second - bl.second).pow(2))
        val maxHeight = max(heightA, heightB).toDouble()

        val mat = editedBitmap.value.toMat()
        val dst = Mat(Size(maxWidth, maxHeight), CvType.CV_8UC3)

        val srcPoints = listOf(
            Point(tl.first.toDouble(), tl.second.toDouble()),
            Point(tr.first.toDouble(), tr.second.toDouble()),
            Point(bl.first.toDouble(), bl.second.toDouble()),
            Point(br.first.toDouble(), br.second.toDouble())
        )
        val dstPoints = listOf(
            Point(0.0, 0.0),
            Point(maxWidth, 0.0),
            Point(0.0, maxHeight),
            Point(maxWidth, maxHeight)
        )

        val perspectiveTransform = Imgproc.getPerspectiveTransform(
            MatOfPoint2f(*srcPoints.toTypedArray()),
            MatOfPoint2f(*dstPoints.toTypedArray())
        )
        Imgproc.warpPerspective(mat, dst, perspectiveTransform, Size(maxWidth, maxHeight))

        _editedBitmap.value = dst.toBitmap()
        _overlayBitmap.value = clearBitmap()
        updateEditedBitmap()
        reset(resetEditedBitmap = false, resetClicks = false)
    }

    override fun copy() {
        if (editedBitmap.value.isEmpty) return
        addToClipboard(editedBitmap.value.toBufferedImage())
    }

    override fun reset(
        resetEditedBitmap: Boolean,
        resetClicks: Boolean,
        resetOverlay: Boolean
    ) {
        if (resetEditedBitmap) _editedBitmap.value = originalBitmap
        if (resetClicks) clicks.clear()
        if (resetOverlay) _overlayBitmap.value = clearBitmap()
        updateEditedBitmap()
    }

    override fun calculateRatio(displayPictureSize: IntSize) {
        if (displayPictureSize == IntSize(0, 0)) return
        this.displayPictureSize = displayPictureSize
        ratio = editedBitmap.value.width.toFloat() / displayPictureSize.width.toFloat()
        bounds = MathRect(
            Offset(0f, 0f),
            Offset(
                displayPictureSize.width.toFloat() * ratio,
                displayPictureSize.height.toFloat() * ratio
            )
        )
    }

    override fun updateEditedBitmap() {
        _editedBitmap.value = editedBitmap.value.makeClone()
    }

    private fun clearBitmap(): Bitmap {
        val bitmap = Bitmap().apply {
            allocPixels(
                ImageInfo.makeN32(
                    editedBitmap.value.width, editedBitmap.value.height, ColorAlphaType.UNPREMUL
                )
            )
            erase(Color.TRANSPARENT)
        }
        return bitmap
    }

    override fun setOriginalPicture(picture: Bitmap) {
        originalBitmap = picture
        reset()
    }

    override fun drawPolygon(pairs: MutableList<Pair<Float, Float>>) {
        if (pairs.isEmpty()) throw IllegalArgumentException("At least one point is required")
        val path = Path().apply {
            moveTo(pairs[0].first, pairs[0].second)
            for (i in 1 until pairs.size) {
                lineTo(pairs[i].first, pairs[i].second)
            }
        }.closePath()
        Canvas(_overlayBitmap.value).drawPath(path, Paints.STROKE)
        updateEditedBitmap()
    }

    override fun drawCircle(pair: Pair<Float, Float>, filled: Boolean) {
        val canvas = Canvas(_overlayBitmap.value)
        canvas.drawCircle(
            pair.first,
            pair.second,
            UnstratifiedValues.CIRCLE_RADIUS_STROKE,
            Paints.STROKE.apply { strokeWidth = UnstratifiedValues.STROKE_WIDTH })
        if (filled) canvas.drawCircle(
            pair.first,
            pair.second,
            UnstratifiedValues.CIRCLE_RADIUS_FILL,
            Paints.FILL
        )
        updateEditedBitmap()
    }

    override fun redrawAllPoints() {
        reset(resetEditedBitmap = false, resetClicks = false)
        for (point in clicks) {
            drawCircle(point, filled = true)
        }
    }
}