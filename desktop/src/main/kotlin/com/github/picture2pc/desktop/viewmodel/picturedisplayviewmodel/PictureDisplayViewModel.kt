package com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.github.picture2pc.desktop.net.datatransmitter.DataReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorFilter
import org.jetbrains.skia.ColorMatrix
import org.jetbrains.skia.Image
import org.jetbrains.skia.Image.Companion.makeFromBitmap
import org.jetbrains.skia.Image.Companion.makeFromEncoded
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode
import org.jetbrains.skia.Point
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.coroutines.CoroutineContext

class PictureDisplayViewModel(
    dataReceiver: DataReceiver,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    private val pictures = dataReceiver.pictures

    val totalPictures = MutableStateFlow(0)
    val currentPictureIndex: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _currentPicture = MutableStateFlow<Image?>(null)
    val currentPicture: StateFlow<Image?> = _currentPicture

    var currentPictureEditor = currentPicture.value?.let { PictureEditor(this, it) }

    init {
        CoroutineScope(coroutineContext).launch {
            pictures.collect { pictureList ->
                totalPictures.value = pictures.replayCache.size
                if (!pictureList.isEmpty && _currentPicture.value == null) {
                    updateCurrentPicture()
                }
            }
        }
    }

    fun getOriginalImage(): Image{
        return try {
            pictures.replayCache[currentPictureIndex.value]
        } catch (e: IndexOutOfBoundsException) {
            getTestImage()
        }
    }

    private fun updateCurrentPictureEditor() {
        currentPictureEditor = currentPicture.value?.let { PictureEditor(this, it) }
    }

    private fun updateCurrentPicture() {
        _currentPicture.value = pictures.replayCache[currentPictureIndex.value]
        updateCurrentPictureEditor()
    }

    private fun Image.toBufferedImage(): BufferedImage {
        val byteArray = this.encodeToData()!!.bytes
        return ImageIO.read(byteArray.inputStream())
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

    fun adjustCurrentPictureIndex(increase:Boolean) {
        if (pictures.replayCache.isEmpty()) return
        if (increase && currentPictureIndex.value < pictures.replayCache.size - 1) {
            currentPictureIndex.value++
        } else if (!increase && currentPictureIndex.value > 0) {
            currentPictureIndex.value--
        }
        updateCurrentPicture()
    }

    fun addPictureToClipboard() {
        if (currentPicture.value == null) return
        val bufferedImage = currentPicture.value?.toBufferedImage()
        val transferableImage = bufferedImage?.let { TransferableImage(it) }
        val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(transferableImage, null)
    }

    private fun getTestImage(): Image {
        val file = File("common/src/main/res/icons/test.png")
        val bufferedImage = ImageIO.read(file)
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return makeFromEncoded(imageBytes)
    }

    fun loadTestImage() {
        val file = File("common/src/main/res/icons/test.png")
        val bufferedImage = ImageIO.read(file)
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        val testImage = makeFromEncoded(imageBytes)

        // Add the test image to the pictures flow
        CoroutineScope(coroutineContext).launch {
            val currentPictures = pictures.replayCache.toMutableList()
            currentPictures.add(testImage)
            _currentPicture.value = testImage
            totalPictures.value = currentPictures.size
            updateCurrentPictureEditor()
        }
    }

    class PictureEditor(private val picDisVM: PictureDisplayViewModel, image: Image) {
        private val bitmap = Bitmap.makeFromImage(image)
        private val canvas: Canvas = Canvas(bitmap)
        private var ratio = 1f
        private var clicks = mutableListOf<Point>()

        private val paint: Paint = Paint().apply {
            color = Color.RED
            mode = PaintMode.STROKE
            strokeWidth = 5f
        }

        fun setDisplaySize(size: IntSize) {
            ratio = bitmap.width / size.width.toFloat()
        }

        fun resetCanvas(clearClicks: Boolean = false) {
            if (clearClicks) clicks.clear()
            canvas.drawImage(picDisVM.getOriginalImage(), 0f, 0f)
            picDisVM._currentPicture.value = makeFromBitmap(bitmap)
        }

        fun clicked(offset: Offset) {
            if (clicks.size == 0) resetCanvas(true)
            clicks.add( Point(offset.x * ratio, offset.y * ratio))
            canvas.drawCircle(offset.x * ratio, offset.y * ratio, 10f, paint)
            if (clicks.size == 4){
                resetCanvas()
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
                    ), paint
                )
                clicks.clear()
            }
            picDisVM._currentPicture.value = makeFromBitmap(bitmap)
        }

        fun crop() {

        }

        fun contrast() {
            val contrastFactor = 2f
            val colorMatrix = ColorMatrix(
                contrastFactor, 0f, 0f, 0f, 0f,
                0f, contrastFactor, 0f, 0f, 0f,
                0f, 0f, contrastFactor, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
            val paint = Paint().apply {
                colorFilter = ColorFilter.makeMatrix(colorMatrix)
            }
            canvas.drawImage(picDisVM.getOriginalImage(), 0f, 0f, paint)
            picDisVM._currentPicture.value = makeFromBitmap(bitmap)
        }
    }
}