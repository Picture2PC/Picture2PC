package com.github.picture2pc.desktop.viewmodel.pictureviewmodel

import com.github.picture2pc.desktop.net.datatransmitter.DataReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.jetbrains.skia.Image
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class PictureViewModel(
    dataReceiver: DataReceiver
) {
    private val pictures = dataReceiver.pictures
    private var currentPictureIndex : Int = 0

    private val _currentPicture = MutableStateFlow<Image?>(null)
    val currentPicture: StateFlow<Image?> = _currentPicture

    init {
        if (pictures.replayCache.isNotEmpty()) {
            _currentPicture.value = pictures.replayCache[currentPictureIndex]
        }
    }

    fun ajustCurrentPictureIndex(increase:Boolean) {
        if (increase && currentPictureIndex < pictures.replayCache.size - 1) {
            currentPictureIndex++
        } else if (!increase && currentPictureIndex > 0) {
            currentPictureIndex--
        }
        _currentPicture.value = pictures.replayCache[currentPictureIndex]
        println(currentPictureIndex)
    }

    fun addPictureToClipboard() {
        runBlocking {
            val image = pictures.replayCache[currentPictureIndex]
            val bufferedImage = image.toBufferedImage()
            val transferableImage = TransferableImage(bufferedImage)
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(transferableImage, null)
        }
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

    fun testAction() {
        println(pictures.replayCache.size)
    }
}