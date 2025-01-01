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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CameraViewModel(
    private val pictureManager: PictureManager,
    private val dataTransmitter: DataTransmitter
) : ViewModel() {
    val takenImage = pictureManager.takenImages.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _flashMode: MutableStateFlow<FlashStates> = MutableStateFlow(FlashStates.FLASH_OFF)
    val flashMode: StateFlow<FlashStates> get() = _flashMode.asStateFlow()

    private var lastCorners: List<Pair<Float, Float>>? = null

    private var galleryCorners: List<Pair<Float, Float>>? = null
    private var isGalleryPicture: Boolean = false

    val pictureCorners: StateFlow<DetectedBox?>
        get() { return pictureManager.pictureCorners }

    private fun getLastImage(): Bitmap {
        return pictureManager.takenImages.replayCache.last()
    }

    fun setViewFinder(previewView: PreviewView) {
        pictureManager.setViewFinder(previewView)
    }

    fun takeImage() {
        isGalleryPicture = false
        lastCorners =
            pictureCorners.value?.pointsBox?.map { Pair(it.x.toFloat(), it.y.toFloat()) }
        pictureManager.takeImage()
    }

    fun injectImage(bitmap: Bitmap, corners: DetectedBox?) {
        isGalleryPicture = true
        galleryCorners = corners?.pointsBox?.map { Pair(it.x.toFloat(), it.y.toFloat()) }
        viewModelScope.launch { pictureManager.injectImage(bitmap) }
    }

    fun sendImage() {
        if (takenImage.value == null)
            return
        viewModelScope.launch {
            val points = if (isGalleryPicture) galleryCorners else  takenImage.value!!.second.await()?.pointsBox?.map {
                Pair(
                    it.x.toFloat(),
                    it.y.toFloat()
                )
            }
            println(points)
            dataTransmitter.sendPicture(
                TcpPayload.Picture(
                    takenImage.value!!.first.toByteArray(),
                    points
                )
            )
        }
    }

    fun switchFlashMode() {
        pictureManager.switchFlashMode()
        _flashMode.value = flashMode.value.next()
    }
}