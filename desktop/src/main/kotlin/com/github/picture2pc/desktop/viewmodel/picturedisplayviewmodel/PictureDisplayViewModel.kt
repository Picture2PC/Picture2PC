package com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel

import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.net.datatransmitter.DataReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.skia.Image.Companion.makeFromEncoded
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.coroutines.CoroutineContext

class PictureDisplayViewModel(
    val dataReceiver: DataReceiver,
    val picturePreparation: PicturePreparation,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    private val pictures = dataReceiver.pictures

    //MutableStateFlow because needs to be updated for Screen
    val totalPictures = MutableStateFlow(0)
    val selectedPictureIndex: MutableStateFlow<Int> = MutableStateFlow(0)

    val currentPicture = picturePreparation.editedBitmap
    val overlayPicture = picturePreparation.overlayBitmap
    val dragOverlayPicture = picturePreparation.dragOverlayBitmap
    val zoomedBitmap = picturePreparation.zoomedBitmap

    init {
        pictures.onEach {
            if (totalPictures.value == 0) picturePreparation.setOriginalPicture(it.toComposeImageBitmap().asSkiaBitmap())
            totalPictures.value = pictures.replayCache.size
        }.launchIn(this)
    }

    fun adjustCurrentPictureIndex(amount: Int) {
        if (pictures.replayCache.isEmpty()) return
        val newIndex = selectedPictureIndex.value + amount
        if (newIndex < 0 || newIndex > pictures.replayCache.size) return

        selectedPictureIndex.value = newIndex
        picturePreparation.setOriginalPicture(pictures.replayCache[newIndex].toComposeImageBitmap().asSkiaBitmap())
    }

    fun loadTestImage() {
        val file = File("common/src/main/res/icons/test3.png")
        val bufferedImage = ImageIO.read(file)
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        val testImage = makeFromEncoded(imageBytes)

        // Add the test image to the pictures flow
        CoroutineScope(coroutineContext).launch {
            dataReceiver.addPicture(testImage)
        }
    }

    /* OLD PICTURE PREPARATION CODE
    class PictureEditor(private val picDisVM: PictureDisplayViewModel, image: Image) {
        private val bitmap = Bitmap.makeFromImage(image)
        private val canvas: Canvas = Canvas(bitmap)
        private var ratio = 1f
        private var clicks = mutableListOf<Point>()

        private val standardPaint: Paint = Paint().apply {
            color = Color.RED
            mode = PaintMode.STROKE
            strokeWidth = 5f
        }

        fun setDisplaySize(size: IntSize) {
            ratio = bitmap.width / size.width.toFloat()
        }

        private fun updateDisplayImage(bitmap: Bitmap = this.bitmap) {
            picDisVM._currentPicture.value = makeFromBitmap(bitmap)
        }

        fun resetCanvas(clearClicks: Boolean = false) {
            if (clearClicks) clicks.clear()
            canvas.drawImage(picDisVM.getOriginalImage(), 0f, 0f)
            picDisVM._currentPicture.value = makeFromBitmap(bitmap)
        }

        fun clicked(offset: Offset) {
            if (clicks.size == 4) {
                clicks.clear()
                resetCanvas()
            }
            clicks.add( Point(offset.x * ratio, offset.y * ratio))
            canvas.drawCircle(offset.x * ratio, offset.y * ratio, 10f, standardPaint)
            if (clicks.size == 4){
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
                    ), standardPaint
                )
            }
            updateDisplayImage()
        }

        fun crop() {
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
                makeFromBitmap(Bitmap.makeFromImage(picDisVM.getOriginalImage())),
                Rect.makeLTRB(minX, minY, maxX, maxY),
                Rect.makeWH(width.toFloat(), height.toFloat())
            )

            // Update the current picture with the cropped image
            updateDisplayImage(croppedBitmap)
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
            canvas.drawRect(Rect.makeWH(bitmap.width.toFloat(), bitmap.height.toFloat()), paint)
            updateDisplayImage()
        }
        */
}