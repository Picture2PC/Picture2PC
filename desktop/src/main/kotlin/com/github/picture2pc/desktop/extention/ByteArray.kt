package com.github.picture2pc.desktop.extention

import org.jetbrains.skia.Image
import java.util.Base64

fun String.toImage(): Image {
    return Image.makeFromEncoded(Base64.getDecoder().decode(this))
}