package com.github.picture2pc.android.data.edgedetection

import android.content.Context
import android.graphics.Bitmap
import org.opencv.core.Mat

interface EdgeDetect {
    fun load(context: Context)
    fun detect(bit: Bitmap): Mat
}