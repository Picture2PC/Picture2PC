package com.github.picture2pc.android.viewmodel.camerascreenviewmodels

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import com.github.picture2pc.android.data.takeimage.PictureManager
import kotlinx.coroutines.flow.SharedFlow

class CameraViewModel(
    private val pictureManager: PictureManager
){
    val takenImages: SharedFlow<Bitmap>
        get() { return  pictureManager.takenImages }

    fun setViewFinder(previewView: PreviewView) {
        pictureManager.setViewFinder(previewView)
    }

    fun takeImage() {
        pictureManager.takeImage()
    }
    fun sendImage(){
        pictureManager.saveImageToCache()
    }

    fun getBlankImage(): Bitmap {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
}