package com.github.picture2pc.desktop.net.datatransmitter

import kotlinx.coroutines.flow.SharedFlow
import org.jetbrains.skia.Image

interface DataReceiver {
    val pictures: SharedFlow<Image>

    suspend fun addPicture(image: Image)
}