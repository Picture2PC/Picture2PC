package com.github.picture2pc.android.data.takeimage

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import kotlinx.coroutines.flow.SharedFlow

interface ImageManager {
    val takenImages : SharedFlow<Bitmap>
    fun takeImage()
    fun getImage() : Bitmap
    fun setViewFinder(viewFinder:PreviewView)
}