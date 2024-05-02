package com.github.picture2pc.android.viewmodel.camerascreenviewmodels

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import com.github.picture2pc.android.data.takeimage.ImageManager
import kotlinx.coroutines.flow.SharedFlow

class CameraViewModel(
    private val imageManager: ImageManager
) {
    val takenImages: SharedFlow<Bitmap>
        get() {
            return  imageManager.takenImages
        }

    fun setViewFinder(previewView: PreviewView) {
        imageManager.setViewFinder(previewView)
    }

    fun takeImage() {
        imageManager.takeImage()
    }
    fun sendImage(){
        //TODO
    }

    fun getBlankImage(): Bitmap {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
}