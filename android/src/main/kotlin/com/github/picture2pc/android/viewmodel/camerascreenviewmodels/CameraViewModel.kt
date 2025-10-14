package com.github.picture2pc.android.viewmodel.camerascreenviewmodels

import android.graphics.Bitmap
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
import com.github.picture2pc.common.ui.notification.NotificationHandler
import com.github.picture2pc.common.ui.notification.NotificationMessages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CameraViewModel(
    private val pictureManager: PictureManager,
    private val dataTransmitter: DataTransmitter,
    private val notificationHandler: NotificationHandler
) : ViewModel() {
    val takenImage =
        pictureManager.takenImages.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _flashMode: MutableStateFlow<FlashStates> = MutableStateFlow(FlashStates.FLASH_OFF)
    val flashMode: StateFlow<FlashStates> get() = _flashMode.asStateFlow()

    private val _sendAvailable = MutableStateFlow(true)
    val sendAvailable = _sendAvailable.asStateFlow()

    private var lastCorners: List<Pair<Float, Float>>? = null

    init {
        pictureManager.takenImages.onEach {
            _pictureCorners.value = null
            _pictureCorners.value = it.second.await() ?: previewCorners.value
        }.launchIn(viewModelScope)
    }

    private val _pictureCorners = MutableStateFlow<DetectedBox?>(null)
    val pictureCorners = _pictureCorners.asStateFlow()
    val previewCorners: StateFlow<DetectedBox?>
        get() {
            return pictureManager.previewCorners
        }

    fun setViewFinder(previewView: PreviewView) {
        pictureManager.setViewFinder(previewView)
    }

    fun takeImage() {
        lastCorners = previewCorners.value?.pointsBox?.map { Pair(it.x.toFloat(), it.y.toFloat()) }
        pictureManager.takeImage()
    }

    fun injectImage(bitmap: Bitmap) {
        pictureManager.injectImage(bitmap)
    }

    fun sendImage() {
        if (takenImage.value == null || !sendAvailable.value) return
        _sendAvailable.value = false
        viewModelScope.launch {
            var newCorners = takenImage.value!!.second.await()?.pointsBox?.map {
                Pair(
                    it.x.toFloat(),
                    it.y.toFloat()
                )
            }

            if (newCorners == null) newCorners = lastCorners
            val success = dataTransmitter.sendPicture(
                TcpPayload.Picture(
                    takenImage.value!!.first.toByteArray(),
                    newCorners
                )
            )
            lastCorners = null
            _sendAvailable.value = true
            val message =
                if (success) NotificationMessages.PICTURE_SENT_SUCCESSFULLY
                else NotificationMessages.PICTURE_NOT_SENT
            notificationHandler.displayNotification(NotificationMessages.TITLE, message, true)
        }
    }

    fun switchFlashMode() {
        pictureManager.switchFlashMode()
        _flashMode.value = flashMode.value.next()
    }
}