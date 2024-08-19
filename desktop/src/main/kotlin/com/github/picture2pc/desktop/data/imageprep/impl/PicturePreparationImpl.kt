package com.github.picture2pc.desktop.data.imageprep.impl

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.desktop.data.deleteTempImage
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.isInBounds
import com.github.picture2pc.desktop.extention.toBitmap
import com.github.picture2pc.desktop.extention.toImage
import com.github.picture2pc.desktop.extention.toMat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorFilter
import org.jetbrains.skia.ColorMatrix
import org.jetbrains.skia.IRect
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode
import org.jetbrains.skia.Path
import org.jetbrains.skiko.toBitmap
import org.jetbrains.skiko.toBufferedImage
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.image.BufferedImage
import kotlin.coroutines.CoroutineContext
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
import androidx.compose.ui.geometry.Rect as MathRect
import org.jetbrains.skia.Point as SkPoint

class PicturePreparationImpl(
    override val coroutineContext: CoroutineContext
) : PicturePreparation, CoroutineScope {
    //Bitmaps for the original, edited, overlay and drag overlay images
    override var originalBitmap: Bitmap = Bitmap()

    private var _editedBitmap: MutableState<Bitmap> = mutableStateOf(Bitmap())
    override var editedBitmap: State<Bitmap> = _editedBitmap

    private var _overlayBitmap: MutableState<Bitmap> = mutableStateOf(clearBitmap())
    override var overlayBitmap: State<Bitmap> = _overlayBitmap

    private var _dragOverlayBitmap: MutableState<Bitmap> = mutableStateOf(clearBitmap())
    override var dragOverlayBitmap: State<Bitmap> = _dragOverlayBitmap

    override var zoomedBitmap: MutableState<Bitmap> = mutableStateOf(Bitmap())

    //Variables for drag handling
    private var dragStartPoint = SkPoint(0f, 0f)
    override var currentDragPoint: MutableState<SkPoint> = mutableStateOf(SkPoint(0f, 0f))
    override var dragActive: MutableState<Boolean> = mutableStateOf(false)

    //Paints for drawing
    private val stroke = Paint().apply {
        color = Colors.PRIMARY.toArgb()
        strokeWidth = 5f
        mode = PaintMode.STROKE
    }
    private val fill = Paint().apply {
        color = Colors.SECONDARY.toArgb()
        mode = PaintMode.FILL
    }

    //Important other variables
    override var ratio: Float = 1f
    override val clicks: MutableList<SkPoint> = mutableListOf()
    private var originalBitmapBounds = MathRect(Offset(0f, 0f), 0f)

    private class TransferableImage(val image: BufferedImage): Transferable {
        override fun getTransferData(flavor: DataFlavor?): Any {
            return if (flavor == DataFlavor.imageFlavor) {
                image
            } else {
                throw UnsupportedFlavorException(flavor)
            }
        }

        override fun getTransferDataFlavors(): Array<DataFlavor> {
            return arrayOf(DataFlavor.imageFlavor)
        }

        override fun isDataFlavorSupported(flavor: DataFlavor?): Boolean {
            return flavor == DataFlavor.imageFlavor
        }
    }

    override fun calculateRatio(displayPictureSize: IntSize) {
        ratio = originalBitmap.width.toFloat() / displayPictureSize.width.toFloat()
    }

    override fun calculateOffset(): Pair<Dp, Dp> {
        return Pair(
            (currentDragPoint.value.x / ratio).dp - 200.dp,
            (currentDragPoint.value.y / ratio).dp - 450.dp
        )
    }

    private fun updateBitmaps(){
        _editedBitmap.value = editedBitmap.value.makeClone()
    }

    private fun clearBitmap(): Bitmap{
        val imageInfo = ImageInfo.makeN32(
            originalBitmap.width, originalBitmap.height, ColorAlphaType.UNPREMUL
        )
        val bitmap = Bitmap()
        bitmap.allocPixels(imageInfo)
        bitmap.erase(Color.TRANSPARENT)

        return bitmap
    }

    override fun applyContrast() {
        val contrast = 1.6f
        val cM = ColorMatrix(
            contrast, 0f, 0f, 0f, 0f,
            0f, contrast, 0f, 0f, 0f,
            0f, 0f, contrast, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        val paint = Paint().apply { colorFilter = ColorFilter.makeMatrix(cM) }

        Canvas(_editedBitmap.value.makeClone())
            .drawImage(editedBitmap.value.toImage(), 0f, 0f, paint)
        updateBitmaps()
    }

    override fun crop() {
        if (clicks.size != 4) return

        val tl = clicks[0] // Top Left
        val tr = clicks[1] // Top Right
        val br = clicks[2] // Bottom Right
        val bl = clicks[3] // Bottom Left

        val widthA = sqrt((tr.x - tl.x).pow(2) + (tr.y - tl.y).pow(2))
        val widthB = sqrt((br.x - bl.x).pow(2) + (br.y - bl.y).pow(2))
        val maxWidth = max(widthA, widthB).toDouble()

        val heightA = sqrt((tr.x - br.x).pow(2) + (tr.y - br.y).pow(2))
        val heightB = sqrt((tl.x - bl.x).pow(2) + (tl.y - bl.y).pow(2))
        val maxHeight = max(heightA, heightB).toDouble()

        val mat = editedBitmap.value.toMat()
        val dst = Mat(Size(maxWidth, maxHeight), CvType.CV_8UC3)

        val srcPoints = listOf(
            Point(tl.x.toDouble(), tl.y.toDouble()),
            Point(tr.x.toDouble(), tr.y.toDouble()),
            Point(bl.x.toDouble(), bl.y.toDouble()),
            Point(br.x.toDouble(), br.y.toDouble())
        )

        val dstPoints = listOf(
            Point(0.0, 0.0),
            Point(maxWidth, 0.0),
            Point(0.0, maxHeight),
            Point(maxWidth, maxHeight)
        )

        val perspectiveTransform = Imgproc.getPerspectiveTransform(MatOfPoint2f(*srcPoints.toTypedArray()), MatOfPoint2f(*dstPoints.toTypedArray()))
        Imgproc.warpPerspective(mat, dst, perspectiveTransform, Size(maxWidth, maxHeight))

        _editedBitmap.value = dst.toBitmap()
        deleteTempImage()
    }

    override fun copyToClipboard() {
        if (editedBitmap.value.isEmpty) return
        CoroutineScope(coroutineContext).launch {
            val transferableImage = TransferableImage(editedBitmap.value.toBufferedImage())
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(transferableImage, null)
        }
    }

    private fun setZoomBitmap(point: SkPoint){
        val x = point.x.toInt()
        val y = point.y.toInt()
        val radius = 75

        val rect = IRect.makeLTRB(
            x - radius,
            y - radius,
            x + radius,
            y + radius
        )

        editedBitmap.value.extractSubset(zoomedBitmap.value, rect)
    }

    override fun setOriginalPicture(picture: Bitmap) {
        originalBitmap = picture
        originalBitmapBounds = MathRect(
            Offset(0f, 0f),
            Offset(picture.width.toFloat(), picture.height.toFloat())
        )
        reset()
    }

    override fun reset(
        resetEditedBitmap: Boolean,
        resetClicks: Boolean,
        resetOverlay: Boolean,
        resetDragOverlay: Boolean
    ) {
        if (resetEditedBitmap) _editedBitmap.value = originalBitmap.toBufferedImage().toBitmap()
        if (resetClicks) clicks.clear()
        if (resetOverlay) _overlayBitmap.value = clearBitmap()
        if (resetDragOverlay) _dragOverlayBitmap.value = clearBitmap()
    }

    override fun resetDrag() {
        handleClick(Offset(currentDragPoint.value.x / ratio, currentDragPoint.value.y / ratio))
        reset(resetOverlay = false, resetClicks = false, resetEditedBitmap = false)
        dragStartPoint = SkPoint(0f, 0f)
        currentDragPoint.value = SkPoint(0f, 0f)
        dragActive.value = false
    }

    override fun setDragStart(dragStart: Offset) {
        dragStartPoint = SkPoint(
            dragStart.x * ratio,
            dragStart.y * ratio
        )
        setZoomBitmap(dragStartPoint)
        dragActive.value = true
    }

    override fun handleDrag(change: PointerInputChange, dragAmount: Offset){
        if (currentDragPoint.value == SkPoint(0f, 0f)) currentDragPoint.value = dragStartPoint

        currentDragPoint.value = SkPoint(
            currentDragPoint.value.x + dragAmount.x * ratio,
            currentDragPoint.value.y + dragAmount.y * ratio
        )
        dragActive.value = currentDragPoint.value.isInBounds(originalBitmapBounds)
        setZoomBitmap(currentDragPoint.value)


        //DEBUG OPTION: Print the current drag point
        val canvas = Canvas(_dragOverlayBitmap.value)
        canvas.drawCircle(currentDragPoint.value.x, currentDragPoint.value.y, 3f, stroke)

        updateBitmaps()
    }

    private fun sortClicksToRectangle() {
        if (clicks.size != 4) return

        // Calculate the centroid of the four points
        val centroid = SkPoint(
            clicks.map { it.x }.average().toFloat(),
            clicks.map { it.y }.average().toFloat()
        )

        // Sort points based on the angle with the centroid
        clicks.sortBy { point ->
            atan2((point.y - centroid.y).toDouble(), (point.x - centroid.x).toDouble())
        }
    }

    private fun drawPolygon(canvas: Canvas, points: List<SkPoint>, paint: Paint) {
        if (points.isEmpty()) throw IllegalArgumentException("At least one point is required")

        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
            closePath()
        }

        canvas.drawPath(path, paint)
    }

    private fun drawCircle(canvas: Canvas, point: SkPoint, filled: Boolean = false) {
        canvas.drawCircle( point.x, point.y, 10f, stroke )
        if (filled) canvas.drawCircle( point.x, point.y, 13f, fill )
    }

    override fun handleClick(offset: Offset) {
        val point = SkPoint(offset.x * ratio, offset.y * ratio)
        if (!point.isInBounds(originalBitmapBounds)) return

        if (clicks.size == 4) reset(resetEditedBitmap = false, resetDragOverlay = false)
        val canvas = Canvas(_overlayBitmap.value)
        if (clicks.isNotEmpty()) drawCircle(canvas, clicks.last(), filled = true)

        clicks.add(point)
        drawCircle(canvas, point, clicks.size >= 4)

        if (clicks.size == 4) {
            sortClicksToRectangle()
            drawPolygon(canvas, clicks, stroke)
        }
        updateBitmaps()
    }
}