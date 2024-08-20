package com.github.picture2pc.desktop.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.launch
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.image.BufferedImage

private class TransferableImage(val image: BufferedImage) : Transferable {
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

@OptIn(DelicateCoroutinesApi::class)
fun addToClipboard(bufferedImage: BufferedImage) {
    CoroutineScope(coroutineContext).launch {
        val transferableImage = TransferableImage(bufferedImage)
        val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(transferableImage, null)
    }
}