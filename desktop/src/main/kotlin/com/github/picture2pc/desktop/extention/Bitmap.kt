package com.github.picture2pc.desktop.extention

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image

fun Bitmap.toImage(): Image {
    return Image.makeFromBitmap(this)
}