package com.github.picture2pc.android.viewmodel.camerascreenviewmodels

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import com.github.picture2pc.android.data.takeimage.PictureManager
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import kotlinx.coroutines.flow.SharedFlow

class CameraViewModel(
    private val pictureManager: PictureManager,
    private val dataTransmitter: DataTransmitter
) {
    val takenImages: SharedFlow<Bitmap>
        get() {
            return pictureManager.takenImages
        }
    fun getLastImage(): Bitmap {
        return pictureManager.takenImages.replayCache.last()
    }

    fun setViewFinder(previewView: PreviewView) {
        pictureManager.setViewFinder(previewView)
    }

    fun takeImage() {
        pictureManager.takeImage()
    }

    fun sendImage() {
        dataTransmitter.send(getLastImage())
    }

}