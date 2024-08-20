package com.github.picture2pc.desktop.extention

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs

fun Mat.toBitmap(): Bitmap {
    val buf = MatOfByte()
    Imgcodecs.imencode(".png", this, buf)
    val image = Image.makeFromEncoded(buf.toArray())
    return Bitmap.makeFromImage(image)
}