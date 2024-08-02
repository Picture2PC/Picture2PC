package com.github.picture2pc.desktop.viewmodel.pictureviewmodel

import com.github.picture2pc.desktop.net.datatransmitter.DataReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private fun updateCurrentPicture() {
        _currentPicture.value = pictures.replayCache[currentPictureIndex]
    }

    fun adjustCurrentPictureIndex(increase:Boolean) {
        if (pictures.replayCache.isEmpty()) return
        if (increase && currentPictureIndex < pictures.replayCache.size - 1) {
            currentPictureIndex++
        } else if (!increase && currentPictureIndex > 0) {
            currentPictureIndex--
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