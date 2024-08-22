package com.github.picture2pc.desktop.extention

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import org.jetbrains.skia.Image.Companion.makeFromEncoded
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

fun Bitmap.toImage(): Image {
    return Image.makeFromBitmap(this)
}

fun makeBitmapFromPath(path: String): Bitmap {
    val file = File(path)
    val bufferedImage = ImageIO.read(file)
    val byteArrayOutputStream = ByteArrayOutputStream()
    ImageIO.write(bufferedImage, "png", byteArrayOutputStream)
    val imageBytes = byteArrayOutputStream.toByteArray()
    val image = makeFromEncoded(imageBytes)

    return Bitmap.Companion.makeFromImage(image)
}

fun Bitmap.toMat(): Mat {
    val byteArray = this.readPixels(this.imageInfo, this.width * this.bytesPerPixel)
    val mat = Mat(this.height, this.width, CvType.CV_8UC4)
    mat.put(0, 0, byteArray)
    return mat
}