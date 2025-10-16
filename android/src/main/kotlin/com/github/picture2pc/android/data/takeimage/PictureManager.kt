package com.github.picture2pc.android.data.takeimage

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import com.github.picture2pc.android.data.edgedetection.DetectedBox
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PictureManager {
    val takenImages: SharedFlow<Pair<Bitmap, Deferred<DetectedBox?>>>
    val previewCorners: StateFlow<DetectedBox?>
    val isProcessing: StateFlow<Boolean>

    fun switchFlashMode()
    fun takeImage()
    fun setViewFinder(previewView: PreviewView)
    fun saveImageToCache()
    fun injectImage(picture: Bitmap)
}