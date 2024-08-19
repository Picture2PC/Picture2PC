package com.github.picture2pc.desktop.extention

import com.github.picture2pc.desktop.data.Variables.PATH_TO_TEMP_FOLDER
import com.github.picture2pc.desktop.data.makeTempDir
import com.github.picture2pc.desktop.data.setHidden
import com.github.picture2pc.desktop.data.tempImageHash
import org.jetbrains.skia.Bitmap
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import java.nio.file.Paths

fun Mat.toBitmap(): Bitmap {
    val hash = this.hashCode()
    val picturePath = "$PATH_TO_TEMP_FOLDER\\${hash}.png"
    makeTempDir()
    
    Imgcodecs.imwrite(picturePath, this)
    setHidden(Paths.get(picturePath))
    tempImageHash = hash

    return makeBitmapFromPath(picturePath)
}