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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CameraViewModel(
    private val pictureManager: PictureManager,
    private val dataTransmitter: DataTransmitter,
    private val notificationHandler: NotificationHandler
) : ViewModel() {


    private val _flashMode: MutableStateFlow<FlashStates> = MutableStateFlow(FlashStates.FLASH_OFF)
    val flashMode: StateFlow<FlashStates> get() = _flashMode.asStateFlow()

    private val _sendAvailable = MutableStateFlow(true)
    val sendAvailable = _sendAvailable.asStateFlow()

    val takenPictures = ArrayDeque<Pair<Bitmap, Deferred<DetectedBox?>>>()

    val takenImage: Pair<Bitmap, Deferred<DetectedBox?>>?
        get() = takenPictures.lastOrNull()

    private var lastCorners: List<Pair<Float, Float>>? = null

    init {
        pictureManager.takenImages.onEach {
            takenPictures.add(it)
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
        takenPictures.clear()
        lastCorners = previewCorners.value?.pointsBox?.map { Pair(it.x.toFloat(), it.y.toFloat()) }
        pictureManager.takeImage()
    }

    fun injectImage(bitmaps: List<Bitmap>) {
        pictureManager.injectImage(bitmaps)
    }

    fun sendImage() {
        if (takenPictures.isEmpty() || !sendAvailable.value) return
        _sendAvailable.value = false
        viewModelScope.launch {
            while (takenPictures.isNotEmpty()) {
                val (bitmap, detectionTask) = takenPictures.removeFirst()

                val newCorners = detectionTask.await()
                    ?.pointsBox
                    ?.map { it.x.toFloat() to it.y.toFloat() }
                    ?: lastCorners

                val success = dataTransmitter.sendPicture(
                    TcpPayload.Picture(bitmap.toByteArray(), newCorners)
                )

                notificationHandler.displayNotification(
                    NotificationMessages.TITLE,
                    if (success) NotificationMessages.PICTURE_SENT_SUCCESSFULLY
                    else NotificationMessages.PICTURE_NOT_SENT,
                    true
                )
                lastCorners = null
            }
            _sendAvailable.value = true
        }
    }

    fun switchFlashMode() {
        pictureManager.switchFlashMode()
        _flashMode.value = flashMode.value.next()
    }
}