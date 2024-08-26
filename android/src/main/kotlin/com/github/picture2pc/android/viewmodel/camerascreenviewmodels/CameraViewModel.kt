package com.github.picture2pc.android.viewmodel.camerascreenviewmodels

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import com.github.picture2pc.android.data.takeimage.PictureManager
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.android.ui.util.FlashStates
import com.github.picture2pc.android.ui.util.next
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewModel(
    private val pictureManager: PictureManager,
    private val dataTransmitter: DataTransmitter
) {
    val takenImages: SharedFlow<Bitmap>
        get() {
            return pictureManager.takenImages
        }

    private val _flashMode: MutableStateFlow<FlashStates> = MutableStateFlow(FlashStates.FLASH_OFF)
    val flashMode: StateFlow<FlashStates> get() = _flashMode.asStateFlow()

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

    fun switchFlashMode() {
        pictureManager.switchFlashMode()
        _flashMode.value = flashMode.value.next()
    }
}