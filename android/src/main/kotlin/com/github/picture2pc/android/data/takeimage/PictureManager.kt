package com.github.picture2pc.android.data.takeimage

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import com.github.picture2pc.android.data.edgedetection.DetectedBox
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PictureManager {
    val takenImages: SharedFlow<Bitmap>
    val pictureCorners: StateFlow<DetectedBox?>

    fun switchFlashMode()
    fun takeImage()
    fun setViewFinder(previewView: PreviewView)
    fun saveImageToCache()
}