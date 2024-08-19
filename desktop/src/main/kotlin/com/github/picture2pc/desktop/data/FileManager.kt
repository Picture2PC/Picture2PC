package com.github.picture2pc.desktop.data

import com.github.picture2pc.desktop.data.Variables.PATH_TO_TEMP_FOLDER
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
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.DosFileAttributeView
import kotlin.io.path.deleteIfExists

object Variables {
    val PATH_TO_TEMP_FOLDER: Path = Paths.get("tmp")
}

var tempImageHash = 0

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

fun setHidden(path: Path){
    Files.getFileAttributeView(path, DosFileAttributeView::class.java).setHidden(true)
}

fun makeTempDir(){
    Files.createDirectories(PATH_TO_TEMP_FOLDER)
    setHidden(PATH_TO_TEMP_FOLDER)
}

fun deleteTempImage(){
    PATH_TO_TEMP_FOLDER.resolve("$tempImageHash.png").deleteIfExists()
}

@OptIn(DelicateCoroutinesApi::class)
fun addToClipboard(bufferedImage: BufferedImage){
    CoroutineScope(coroutineContext).launch {
        val transferableImage = TransferableImage(bufferedImage)
        val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(transferableImage, null)
    }
}