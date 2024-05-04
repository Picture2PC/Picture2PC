package com.github.picture2pc.desktop.extention

import org.jetbrains.skia.Image

fun ByteArray.toImage(): Image {
    return Image.makeFromEncoded(this)
}