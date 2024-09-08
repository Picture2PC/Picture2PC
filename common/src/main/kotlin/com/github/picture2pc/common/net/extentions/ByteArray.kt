package com.github.picture2pc.common.net.extentions

import org.jetbrains.skia.Image

fun ByteArray.toImage(): Image {
    return Image.makeFromEncoded(this)
}