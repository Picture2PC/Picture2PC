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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.IRect
import org.jetbrains.skia.Image.Companion.makeFromBitmap
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode
import org.jetbrains.skia.Point
import org.jetbrains.skia.Rect
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
    private var originalBitmapBorderRectangle = MathRect(Offset(0f, 0f), 0f)

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

    override fun calculateRatio(displayPictureSize: IntSize) {
        ratio = originalBitmap.width.toFloat() / displayPictureSize.width.toFloat()
    }

    override fun calculateOffset(): Pair<Dp, Dp> {
        return Pair(
            (currentDragPoint.value.x / ratio).dp - 200.dp,
            (currentDragPoint.value.y / ratio).dp - 450.dp
        )
    }

    private fun isInRect(point: Point): Boolean {
        return !(
            point.x < originalBitmapBorderRectangle.left ||
            point.x > originalBitmapBorderRectangle.right ||
            point.y < originalBitmapBorderRectangle.top ||
            point.y > originalBitmapBorderRectangle.bottom
        )
    }

    override fun applyContrast() {

    }

    override fun crop() {
        //TODO: Rewrite function to warp translate and then crop the image
        if (clicks.size != 4) return

        // Calculate the bounding box of the square
        val minX = clicks.minOf { it.x }
        val minY = clicks.minOf { it.y }
        val maxX = clicks.maxOf { it.x }
        val maxY = clicks.maxOf { it.y }

        // Define the width and height of the cropped area
        val width = (maxX - minX).toInt()
        val height = (maxY - minY).toInt()

        // Create a new bitmap for the cropped image
        val croppedBitmap = Bitmap().apply {
            allocN32Pixels(width, height)
        }
        val croppedCanvas = Canvas(croppedBitmap)

        // Draw the cropped area onto the new bitmap
        croppedCanvas.drawImageRect(
            makeFromBitmap(originalBitmap),
            Rect.makeLTRB(minX, minY, maxX, maxY),
            Rect.makeWH(width.toFloat(), height.toFloat())
        )

        // Update the current picture with the cropped image
        _editedBitmap.value = croppedBitmap
        updateBitmaps()

        reset(resetEditedBitmap = false, clearClicks = false)
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
        originalBitmap.extractSubset(zoomedBitmap.value, rect)
        //Canvas(zoomedBitmap.value).drawCircle(radius.toFloat(), radius.toFloat(), 10f, blueStroke)
    }

    override fun reset(
        resetEditedBitmap: Boolean,
        clearClicks: Boolean,
        clearOverlay: Boolean,
        clearDragOverlay: Boolean
    ) {
        if (resetEditedBitmap) _editedBitmap.value = originalBitmap
        if (clearClicks) clicks.clear()
        if (clearOverlay) _overlayBitmap.value = clearCanvasBitmap()
        if (clearDragOverlay) _dragOverlayBitmap.value = clearCanvasBitmap()
    }

    override fun resetDrag() {
        addClick(Offset(currentDragPoint.value.x / ratio, currentDragPoint.value.y / ratio))
        reset(clearOverlay = false, clearClicks = false, resetEditedBitmap = false)
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
        dragActive.value = isInRect(currentDragPoint.value)
        setZoomBitmap(currentDragPoint.value)

        /*
        //DEBUG OPTION: Print the current drag point
        val canvas = Canvas(_dragOverlayBitmap.value)
        canvas.drawCircle(currentDragPoint.value.x, currentDragPoint.value.y, 3f, redStroke)
        */
        updateBitmaps()
    }

    override fun setOriginalPicture(picture: Bitmap) {
        originalBitmap = picture

        originalBitmapBorderRectangle = MathRect(
            Offset(0f, 0f),
            Offset(picture.width.toFloat(), picture.height.toFloat())
        )
        reset()
    }

    private fun sortToRectangle() {
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

    private fun drawRectangle(canvas: Canvas){
        canvas.drawLines(
            floatArrayOf(
                clicks[0].x, clicks[0].y,
                clicks[1].x, clicks[1].y,

                clicks[1].x, clicks[1].y,
                clicks[2].x, clicks[2].y,

                clicks[2].x, clicks[2].y,
                clicks[3].x, clicks[3].y,

                clicks[3].x, clicks[3].y,
                clicks[0].x, clicks[0].y
            ), blueStroke
        )
    }

    private fun drawCircle(canvas: Canvas, point: Point){
        canvas.drawCircle( point.x, point.y, 10f, blueStroke )
    }

    private fun drawFullCircle(canvas: Canvas){
        clicks.last().let { point ->
            canvas.drawCircle( point.x, point.y, 10f, blueFill )
        }
    }

    override fun addClick(offset: Offset) {
        val point = Point(offset.x * ratio, offset.y * ratio)
        val canvas = Canvas(_overlayBitmap.value)

        if (!isInRect(point)) return
        if (clicks.size == 4) reset(resetEditedBitmap = false)
        if (clicks.isNotEmpty()) drawFullCircle(canvas)

        clicks.add(point)
        drawCircle(canvas, point)

        if (clicks.size == 4){
            drawFullCircle(canvas)
            sortToRectangle()
            reset(clearClicks = false, clearOverlay = false)
            drawRectangle(canvas)
        }

        updateBitmaps()
    }
}