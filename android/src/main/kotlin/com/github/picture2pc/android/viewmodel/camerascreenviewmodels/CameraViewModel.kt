package com.github.picture2pc.android.viewmodel.camerascreenviewmodels

import android.graphics.Bitmap
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.picture2pc.android.data.edgedetection.DetectedBox
import com.github.picture2pc.android.data.takeimage.PictureManager
import com.github.picture2pc.android.extentions.toByteArray
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.android.ui.util.FlashStates
import com.github.picture2pc.android.ui.util.next
import com.github.picture2pc.common.net.data.payload.TcpPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel(
    private val pictureManager: PictureManager,
    private val dataTransmitter: DataTransmitter,
) : ViewModel() {
    val takenImage: SharedFlow<Bitmap>
        get() {
            return pictureManager.takenImages
        }

    private val _flashMode: MutableStateFlow<FlashStates> = MutableStateFlow(FlashStates.FLASH_OFF)
    val flashMode: StateFlow<FlashStates> get() = _flashMode.asStateFlow()

    private var lastCorners: List<Pair<Float, Float>>? = null

    val pictureCorners: StateFlow<DetectedBox?>
        get() {
            return pictureManager.pictureCorners
        }

    fun getLastImage(): Bitmap {
        return pictureManager.takenImages.replayCache.last()
    }

    fun setViewFinder(previewView: PreviewView) {
        pictureManager.setViewFinder(previewView)
    }

    fun takeImage() {
        lastCorners =
            pictureCorners.value?.pointsBox?.map { Pair(it.x.toFloat(), it.y.toFloat()) }
        pictureManager.takeImage()
    }

    fun sendImage(context: android.content.Context) {
        val launch = viewModelScope.launch {
                dataTransmitter.sendPicture(
                    TcpPayload.Picture(
                        getLastImage().toByteArray(),
                        lastCorners
                    )
                )
        }
        Toast.makeText(context, "Image sending...", Toast.LENGTH_SHORT).show()
        launch.invokeOnCompletion {
            if (launch.isCompleted) {
                Toast.makeText(context, "Image sent", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun switchFlashMode() {
        pictureManager.switchFlashMode()
        _flashMode.value = flashMode.value.next()
    }
}