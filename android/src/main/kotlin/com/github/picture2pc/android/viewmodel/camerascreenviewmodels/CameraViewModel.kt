package com.github.picture2pc.android.viewmodel.camerascreenviewmodels

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import com.github.picture2pc.android.data.takeimage.ImageManager

class CameraViewModel(
    private val imageManager: ImageManager
) {
    fun getLastImage(): Bitmap {
        return if (imageManager.takenImages.replayCache.isNotEmpty()) {
            imageManager.takenImages.replayCache.last()
        } else {
            return imageManager.getImage()
        }
    }

    fun setViewFinder(previewView: PreviewView) {
        imageManager.setViewFinder(previewView)
    }

    fun setTestImage(){
        imageManager.setTestImage()
    }

    fun takeImage() {
        imageManager.takeImage()
    }
    fun sendImage(){
        /*TODO*/
    }
}