package com.github.picture2pc.android.viewmodel.camerascreenviewmodels

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import com.github.picture2pc.android.data.takeimage.ImageManager

class CameraViewModel(
    private val cameraImageManager: ImageManager
) {
    fun getLastImage(): Bitmap {
        return cameraImageManager.takenImages.replayCache.last()
    }

    fun getAllImages() {
        /*TODO*/
    }

    fun setViewFinder(viewFinder: PreviewView) {
        cameraImageManager.viewFinder = viewFinder
    }

    fun takeImage() {
        cameraImageManager.takeImage()
    }
}