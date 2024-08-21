package com.github.picture2pc.android.ui.util

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

fun getIcon(path: String): ImageBitmap? {
    //TODO: Fix, the byte array is always null
    val byteArray = object {}.javaClass.getResourceAsStream(path)?.readBytes()
    if (byteArray != null) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size).asImageBitmap()
    }
    return null
}