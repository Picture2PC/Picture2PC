package com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel

import com.github.picture2pc.desktop.net.datatransmitter.DataReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.skia.Image
import org.jetbrains.skia.Image.Companion.makeFromEncoded
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
    val pictures = dataReceiver.pictures

    val totalPictures = MutableStateFlow<Int>(0)
    val currentPictureIndex: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _currentPicture = MutableStateFlow<Image?>(null)
    val currentPicture: StateFlow<Image?> = _currentPicture

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

    private fun updateCurrentPicture() {
        _currentPicture.value = pictures.replayCache[currentPictureIndex.value]
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

    fun getPictureDimensions(): Pair<Int, Int> {
        return Pair(currentPicture.value?.width ?: 0, currentPicture.value?.height ?: 0)
    }

    fun loadTestImage() {
        val file = File("common/src/main/resources/icons/test.png")
        val bufferedImage = ImageIO.read(file)
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        val skiaImage = makeFromEncoded(imageBytes)
        _currentPicture.value = skiaImage
    }

    fun getSelectedInfo(): String {
        if (pictures.replayCache.isEmpty()) return "0 / 0"
        return "${currentPictureIndex.value + 1} / ${pictures.replayCache.size}"
    }

    fun testAction() {
        println(pictures.replayCache.size)
    }
}