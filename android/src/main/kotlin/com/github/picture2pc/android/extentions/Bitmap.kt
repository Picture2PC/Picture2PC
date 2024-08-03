package com.github.picture2pc.android.extentions

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.util.Base64

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}

fun Bitmap.toBase64(): String {
    return String(Base64.getEncoder().encode(toByteArray()))
}