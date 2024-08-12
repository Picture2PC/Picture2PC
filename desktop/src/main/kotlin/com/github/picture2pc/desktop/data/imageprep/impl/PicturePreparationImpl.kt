package com.github.picture2pc.desktop.data.imageprep.impl

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

class PicturePreparationImpl(
    override val coroutineContext: CoroutineContext
) : PicturePreparation, CoroutineScope {
    override var originalBitmap: Bitmap = Bitmap()

    private var _editedBitmap: MutableStateFlow<Bitmap> = MutableStateFlow(Bitmap())
    override var editedBitmap: StateFlow<Bitmap> = _editedBitmap

    private var _overlayBitmap: MutableStateFlow<Bitmap> = MutableStateFlow(clearCanvasBitmap())
    override var overlayBitmap: StateFlow<Bitmap> = _overlayBitmap

    override var ratio: Float = 1f
    override val clicks: MutableList<Point> = mutableListOf()

    private val blueStroke = Paint().apply {
        color = Color.BLUE
        mode = PaintMode.STROKE
        strokeWidth = 5f
    }

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
        println("crop")
    }

    override fun copyToClipboard() {
        if (overlayBitmap.value.isEmpty) return
        CoroutineScope(coroutineContext).launch {
            val transferableImage = TransferableImage(overlayBitmap.value.toBufferedImage())
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(transferableImage, null)
        }
    }

    override fun reset(clearClicks: Boolean, clearOverlay: Boolean) {
        _editedBitmap.value = originalBitmap
        if (clearOverlay) _overlayBitmap.value = clearCanvasBitmap()
        if (clearClicks) clicks.clear()
    }

    override fun setOriginalPicture(picture: Bitmap) {
        originalBitmap = picture
        _overlayBitmap.value = clearCanvasBitmap()
        reset()
    }

    override fun addClick(offset: Offset) {
        println(offset)
        clicks.add(Point(offset.x, offset.y))
        Canvas(_overlayBitmap.value).drawCircle(
            offset.x * ratio,offset.y * ratio, 10f, blueStroke
        )
    }
}