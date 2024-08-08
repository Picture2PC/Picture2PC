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
import org.jetbrains.skia.ColorInfo
import org.jetbrains.skia.ImageInfo
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
    private var _originalBitmap: Bitmap = Bitmap()
    private var _editedBitmap: MutableStateFlow<Bitmap> = MutableStateFlow(Bitmap())
    private var _overlayBitmap: MutableStateFlow<Bitmap> = MutableStateFlow(clearCanvasBitmap())
    private var _overlayCanvas: Canvas = Canvas(_overlayBitmap.value)

    private var _ratio: Float = 1f

    override var originalBitmap: Bitmap
        get() = _originalBitmap
        set(value) { _originalBitmap = value }

    override var editedPicture: StateFlow<Bitmap>
        get() = _editedBitmap
        set(value) { _editedBitmap.value = value.value }

    override var overlayBitmap: StateFlow<Bitmap>
        get() = _overlayBitmap
        set(value) { _overlayBitmap.value = value.value }

    override val overlayCanvas: Canvas
        get() = _overlayCanvas

    override var ratio: Float
        get() = _ratio
        set(value) { _ratio = value }

    override val clicks: MutableList<Point>
        get() = mutableListOf()

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
        val imageInfo = ImageInfo(ColorInfo.DEFAULT, originalBitmap.width, originalBitmap.height)
        return Bitmap().apply {
            setImageInfo(imageInfo)
            allocPixels()
        }.erase(0)
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
        if (editedPicture.value.isEmpty) return
        CoroutineScope(coroutineContext).launch {
            val transferableImage = TransferableImage(editedPicture.value.toBufferedImage())
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(transferableImage, null)
        }
    }

    override fun reset() {
        _editedBitmap.value = originalBitmap
        _overlayBitmap.value = clearCanvasBitmap()
        _overlayCanvas = Canvas(_overlayBitmap.value)
        clicks.clear()
    }

    override fun setOriginalPicture(picture: Bitmap) {
        originalBitmap = picture
        val imageInfo = ImageInfo(ColorInfo.DEFAULT, originalBitmap.width, originalBitmap.height)
        _overlayBitmap.value = Bitmap().apply {
            setImageInfo(imageInfo)
            allocPixels()
            erase(0)
        }
        reset()
    }

    override fun addClick(offset: Offset) {
        clicks.add(Point(offset.x, offset.y))
        println(offset)
    }
}