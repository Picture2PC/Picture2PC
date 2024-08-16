package com.github.picture2pc.desktop.data.imageprep.impl

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.isInBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.IRect
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode
import org.jetbrains.skia.Path
import org.jetbrains.skia.Point
import org.jetbrains.skiko.toBufferedImage
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.image.BufferedImage
import kotlin.coroutines.CoroutineContext
import kotlin.math.atan2
import androidx.compose.ui.geometry.Rect as MathRect

class PicturePreparationImpl(
    override val coroutineContext: CoroutineContext
) : PicturePreparation, CoroutineScope {
    //Bitmaps for the original, edited, overlay and drag overlay images
    override var originalBitmap: Bitmap = Bitmap()

    private var _editedBitmap: MutableState<Bitmap> = mutableStateOf(Bitmap())
    override var editedBitmap: State<Bitmap> = _editedBitmap

    private var _overlayBitmap: MutableState<Bitmap> = mutableStateOf(clearCanvasBitmap())
    override var overlayBitmap: State<Bitmap> = _overlayBitmap

    private var _dragOverlayBitmap: MutableState<Bitmap> = mutableStateOf(clearCanvasBitmap())
    override var dragOverlayBitmap: State<Bitmap> = _dragOverlayBitmap

    override var zoomedBitmap: MutableState<Bitmap> = mutableStateOf(Bitmap())

    //Variables for drag handling
    private var dragStartPoint = Point(0f, 0f)
    override var currentDragPoint: MutableState<Point> = mutableStateOf(Point(0f, 0f))
    override var dragActive: MutableState<Boolean> = mutableStateOf(false)

    //Paints for drawing
    private val blueStroke = Paint().apply {
        color = Color.BLUE
        strokeWidth = 5f
        mode = PaintMode.STROKE
    }
    private val blueFill = Paint().apply {
        color = Color.BLUE
        strokeWidth = 5f
        mode = PaintMode.FILL
    }

    //Important other variables
    override var ratio: Float = 1f
    override val clicks: MutableList<Point> = mutableListOf()
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

    private fun clearCanvasBitmap(): Bitmap{
        val imageInfo = ImageInfo.makeN32(
            originalBitmap.width, originalBitmap.height, ColorAlphaType.UNPREMUL
        )
        val bitmap = Bitmap()
        bitmap.allocPixels(imageInfo)
        bitmap.erase(Color.TRANSPARENT)

        return bitmap
    }

    override fun applyContrast() {

    }

    override fun crop() {
        if (clicks.size != 4) return
        /*val allX = clicks.map { it.x }
        val allY = clicks.map { it.y }

        val (x1, y1) = clicks[0].x to clicks[0].y
        val (x2, y2) = clicks[1].x to clicks[1].y
        val (x3, y3) = clicks[2].x to clicks[2].y
        val (x4, y4) = clicks[3].x to clicks[3].y
        val (w, h) = max(allX) - min(allX) to max(allY) - min(allY)

        val scaleX = (y1 * x2 * x4 - x1 * y2 * x4 + x1 * y3 * x4 - x2 * y3 * x4 - y1 * x2 * x3 + x1 * y2 * x3 - x1 * y4 * x3 + x2 * y4 * x3) / (x2 * y3 * w + y2 * x4 * w - y3 * x4 * w - x2 * y4 * w - y2 * w * x3 + y4 * w * x3)
        val skewX = (-x1 * x2 * y3 - y1 * x2 * x4 + x2 * y3 * x4 + x1 * x2 * y4 + x1 * y2 * x3 + y1 * x4 * x3 - y2 * x4 * x3 - x1 * y4 * x3) / (x2 * y3 * h + y2 * x4 * h - y3 * x4 * h - x2 * y4 * h - y2 * h * x3 + y4 * h * x3)
        val transX = x1
        val skewY = (-y1 * x2 * y3 + x1 * y2 * y3 + y1 * y3 * x4 - y2 * y3 * x4 + y1 * x2 * y4 - x1 * y2 * y4 - y1 * y4 * x3 + y2 * y4 * x3) / (x2 * y3 * w + y2 * x4 * w - y3 * x4 * w - x2 * y4 * w - y2 * w * x3 + y4 * w * x3)
        val scaleY = (-y1 * x2 * y3 - y1 * y2 * x4 + y1 * y3 * x4 + x1 * y2 * y4 - x1 * y3 * y4 + x2 * y3 * y4 + y1 * y2 * x3 - y2 * y4 * x3) / (x2 * y3 * h + y2 * x4 * h - y3 * x4 * h - x2 * y4 * h - y2 * h * x3 + y4 * h * x3)
        val transY = y1
        val persp0 = (x1 * y3 - x2 * y3 + y1 * x4 - y2 * x4 - x1 * y4 + x2 * y4 - y1 * x3 + y2 * x3) / (x2 * y3 * w + y2 * x4 * w - y3 * x4 * w - x2 * y4 * w - y2 * w * x3 + y4 * w * x3)
        val persp1 = (-y1 * x2 + x1 * y2 - x1 * y3 - y2 * x4 + y3 * x4 + x2 * y4 + y1 * x3 - y4 * x3) / (x2 * y3 * h + y2 * x4 * h - y3 * x4 * h - x2 * y4 * h - y2 * h * x3 + y4 * h * x3)
        val persp2 = 1f

        val matrix = Matrix33(scaleX, skewX, transX, skewY, scaleY, transY, persp0, persp1, persp2)

        Canvas(_editedBitmap.value).setMatrix(matrix).drawImage(originalBitmap.toImage(), 0f, 0f)*/

        

        updateBitmaps()
    }

    override fun copyToClipboard() {
        if (overlayBitmap.value.isEmpty) return
        CoroutineScope(coroutineContext).launch {
            val transferableImage = TransferableImage(overlayBitmap.value.toBufferedImage())
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(transferableImage, null)
        }
    }

    private fun setZoomBitmap(point: Point){
        val x = point.x.toInt()
        val y = point.y.toInt()
        val radius = 75

        val rect = IRect.makeLTRB(
            x - radius,
            y - radius,
            x + radius,
            y + radius
        )
        originalBitmap.makeClone().extractSubset(zoomedBitmap.value, rect)
        Canvas(zoomedBitmap.value).drawCircle(radius.toFloat(), radius.toFloat(), 10f, blueStroke)
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
        if (resetEditedBitmap) _editedBitmap.value = originalBitmap.makeClone()
        if (resetClicks) clicks.clear()
        if (resetOverlay) _overlayBitmap.value = clearCanvasBitmap()
        if (resetDragOverlay) _dragOverlayBitmap.value = clearCanvasBitmap()
    }

    override fun resetDrag() {
        handleClick(Offset(currentDragPoint.value.x / ratio, currentDragPoint.value.y / ratio))
        reset(resetOverlay = false, resetClicks = false, resetEditedBitmap = false)
        dragStartPoint = Point(0f, 0f)
        currentDragPoint.value = Point(0f, 0f)
        dragActive.value = false
    }

    override fun setDragStart(dragStart: Offset) {
        dragStartPoint = Point(
            dragStart.x * ratio,
            dragStart.y * ratio
        )
        setZoomBitmap(dragStartPoint)
        dragActive.value = true
    }

    override fun handleDrag(change: PointerInputChange, dragAmount: Offset){
        if (currentDragPoint.value == Point(0f, 0f)) currentDragPoint.value = dragStartPoint

        currentDragPoint.value = Point(
            currentDragPoint.value.x + dragAmount.x * ratio,
            currentDragPoint.value.y + dragAmount.y * ratio
        )
        dragActive.value = currentDragPoint.value.isInBounds(originalBitmapBounds)
        setZoomBitmap(currentDragPoint.value)

        /*
        //DEBUG OPTION: Print the current drag point
        val canvas = Canvas(_dragOverlayBitmap.value)
        canvas.drawCircle(currentDragPoint.value.x, currentDragPoint.value.y, 3f, redStroke)
        */
        updateBitmaps()
    }

    private fun sortClicksToRectangle() {
        if (clicks.size != 4) return

        // Calculate the centroid of the four points
        val centroid = Point(
            clicks.map { it.x }.average().toFloat(),
            clicks.map { it.y }.average().toFloat()
        )

        // Sort points based on the angle with the centroid
        clicks.sortBy { point ->
            atan2((point.y - centroid.y).toDouble(), (point.x - centroid.x).toDouble())
        }
    }

    private fun drawPolygon(canvas: Canvas, points: List<Point>, paint: Paint) {
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

    private fun drawCircle(canvas: Canvas, point: Point, filled: Boolean = false) {
        println("Drawing circle")
        canvas.drawCircle( point.x, point.y, 10f, if (filled) blueFill else blueStroke )
    }

    override fun handleClick(offset: Offset) {
        val point = Point(offset.x * ratio, offset.y * ratio)
        if (!point.isInBounds(originalBitmapBounds)) return

        if (clicks.size == 4) reset(resetEditedBitmap = false, resetDragOverlay = false)
        val canvas = Canvas(_overlayBitmap.value)
        if (clicks.isNotEmpty()) drawCircle(canvas, clicks.last(), filled = true)

        clicks.add(point)
        drawCircle(canvas, point, clicks.size >= 4)

        if (clicks.size == 4) {
            sortClicksToRectangle()
            drawPolygon(canvas, clicks, blueStroke)
        }
        updateBitmaps()
    }
}