package com.github.picture2pc.android.data.takeimage

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import kotlinx.coroutines.flow.SharedFlow

interface PictureManager {
    val takenImages : SharedFlow<Bitmap>
    fun takeImage()
    fun setViewFinder(previewView: PreviewView)
    fun saveImageToCache()
}