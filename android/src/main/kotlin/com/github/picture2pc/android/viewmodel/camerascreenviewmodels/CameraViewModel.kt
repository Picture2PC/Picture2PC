package com.github.picture2pc.android.viewmodel.camerascreenviewmodels

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.picture2pc.android.data.takeimage.PictureManager
import com.github.picture2pc.android.extentions.toByteArray
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.common.ui.Icons
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class CameraViewModel(
    private val pictureManager: PictureManager,
    private val dataTransmitter: DataTransmitter
) : ViewModel() {
    val takenImages: SharedFlow<Bitmap>
        get() {
            return pictureManager.takenImages
        }

    private var flashState = Icons.Mobile.FLASH_OFF
    private val flashMode by mutableStateOf(flashState)

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
        viewModelScope.launch {
            dataTransmitter.sendPicture(getLastImage().toByteArray())
        }
    }

    fun switchFlashMode() {
        pictureManager.switchFlashMode()
        flashState = if (flashMode == Icons.Mobile.FLASH_AUTO) {
            Icons.Mobile.FLASH_OFF
        } else {
            Icons.Mobile.FLASH_AUTO
        }
    }
}