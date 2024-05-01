package com.github.picture2pc.android.viewmodel.camerascreenviewmodels

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import com.github.picture2pc.android.data.takeimage.ImageManager

class CameraViewModel(
    private val cameraImageManager: ImageManager
) {
    fun getLastImage(): Bitmap {
        return if (cameraImageManager.takenImages.replayCache.isNotEmpty()) {
            cameraImageManager.takenImages.replayCache.last()
        } else {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }
    }

    fun setViewFinder(viewFinder: PreviewView) {
        cameraImageManager.setViewFinder(viewFinder)
    }

    fun takeImage() {
        cameraImageManager.takeImage()
    }
    fun sendImage(){
        /*TODO*/
    }
}