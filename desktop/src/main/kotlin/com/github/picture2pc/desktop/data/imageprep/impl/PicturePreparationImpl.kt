package com.github.picture2pc.desktop.data.imageprep.impl

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.IntSize
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.distanceTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode
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


    //Variables for drag handling
    private var dragStartPoint = Point(0f, 0f)
    private var currentDragPoint = Point(0f, 0f)
    private var previousPoint = Point(0f, 0f)
    private var totalDrag = 0f

    //Paints for drawing
    private val blueStroke = Paint().apply {
        color = Color.BLUE
        strokeWidth = 5f
        mode = PaintMode.STROKE
    }
    private val redStroke = Paint().apply {
        color = Color.RED
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

    private class TransferableImage(private val image: BufferedImage) : Transferable {
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

    private fun updateOverlayBitmaps(
        updateOverlay: Boolean = true,
        updateDragOverlay: Boolean = false
    ){
        if (updateOverlay) _overlayBitmap.value = _overlayBitmap.value.makeClone()
        if (updateDragOverlay) _dragOverlayBitmap.value = _dragOverlayBitmap.value.makeClone()
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
        ratio =  originalBitmap.width.toFloat() / displayPictureSize.width.toFloat()
    }

    override fun applyContrast() {
        println("contrast")
    }

    override fun crop() {
        if (clicks.size != 4) return

        //TODO: Warp translate and crop the image

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
        if (totalDrag < 23f) {
            addClick(Offset(currentDragPoint.x / ratio, currentDragPoint.y / ratio))
        }
        reset(clearOverlay = false, clearClicks = false, resetEditedBitmap = false)
        dragStartPoint = Point(0f, 0f)
        currentDragPoint = Point(0f, 0f)
        previousPoint = Point(0f, 0f)
        totalDrag = 0f
    }

    override fun setDragStart(dragStart: Offset) {
        dragStartPoint = Point(
            dragStart.x * ratio,
            dragStart.y * ratio
        )
    }

    override fun handleDrag(change: PointerInputChange, dragAmount: Offset){
        val canvas = Canvas(_dragOverlayBitmap.value)
        if (currentDragPoint == Point(0f, 0f)) currentDragPoint = dragStartPoint

        currentDragPoint = Point(
            currentDragPoint.x + dragAmount.x * ratio,
            currentDragPoint.y + dragAmount.y * ratio
        )
        if (previousPoint != Point(0f, 0f)) {
            totalDrag += currentDragPoint.distanceTo(previousPoint)
        }
        previousPoint = currentDragPoint

        canvas.drawCircle(currentDragPoint.x, currentDragPoint.y, 3f, redStroke)
        updateOverlayBitmaps(updateOverlay = false, updateDragOverlay = true)
    }

    override fun setOriginalPicture(picture: Bitmap) {
        originalBitmap = picture
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

        if (clicks.size == 4) reset()
        if (clicks.isNotEmpty()) drawFullCircle(canvas)

        clicks.add(point)
        drawCircle(canvas, point)

        if (clicks.size == 4){
            drawFullCircle(canvas)
            sortToRectangle()
            reset(clearClicks = false, clearOverlay = false)
            drawRectangle(canvas)
        }
        //TODO: Notify observers without cloning, possibly use LiveData?
        updateOverlayBitmaps()
    }
}