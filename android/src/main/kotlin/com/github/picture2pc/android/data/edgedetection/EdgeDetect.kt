package com.github.picture2pc.android.data.edgedetection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point

interface EdgeDetect {
    fun load(context: Context)
    fun detect(bit: Bitmap): List<Point>
}