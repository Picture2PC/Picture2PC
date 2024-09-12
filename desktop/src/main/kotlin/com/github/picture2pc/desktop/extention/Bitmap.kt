package com.github.picture2pc.desktop.extention

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import org.opencv.core.CvType
import org.opencv.core.Mat


fun Bitmap.toImage(): Image {
    return Image.makeFromBitmap(this)
}

fun Bitmap.toMat(): Mat {
    val byteArray = this.readPixels(this.imageInfo, this.width * this.bytesPerPixel)
    val mat = Mat(this.height, this.width, CvType.CV_8UC4)
    mat.put(0, 0, byteArray)
    return mat
}