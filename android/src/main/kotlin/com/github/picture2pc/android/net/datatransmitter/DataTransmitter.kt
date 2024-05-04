package com.github.picture2pc.android.net.datatransmitter

import android.graphics.Bitmap

interface DataTransmitter {
    fun send(picture: Bitmap)
}