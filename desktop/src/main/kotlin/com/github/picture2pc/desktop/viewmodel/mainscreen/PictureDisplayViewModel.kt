package com.github.picture2pc.desktop.viewmodel.mainscreen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.desktop.data.RotationState
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.toImage
import com.github.picture2pc.desktop.net.datatransmitter.DataTransmitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PictureDisplayViewModel(
    viewModelScope: CoroutineScope,
    dataReceiver: DataTransmitter,
    private val mHVM: MovementHandlerViewModel,
    private val pP: PicturePreparation,
) {
    private val picture = dataReceiver.picture

    private val pictureQueue: ArrayDeque<TcpPayload.Picture> = ArrayDeque()
    val totalPictures = MutableStateFlow(0)
    val selectedPictureIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentPicture = pP.editedBitmap
    private var displayPictureSize = Size(0f, 0f)

    init {
        picture.onEach {
            pictureQueue.addLast(it)
            if (totalPictures.value == 0) setPicture(it)
            totalPictures.value = pictureQueue.size
        }.launchIn(viewModelScope)
    }


    fun adjustCurrentPictureIndex(amount: Int) {
        var newIndex = selectedPictureIndex.value + amount
        if (pictureQueue.isEmpty() || newIndex !in 0 until pictureQueue.size) return

        if (newIndex == 5) {
            pictureQueue.removeFirst()
            newIndex -= 1
            totalPictures.value -= 1
        }

        selectedPictureIndex.value = newIndex
        setPicture(pictureQueue[selectedPictureIndex.value])

        mHVM.rotationState.value = RotationState.ROTATION_0
    }

    private fun setPicture(payload: TcpPayload.Picture) {
        pP.setOriginalPicture(
            payload.picture.toImage().toComposeImageBitmap().asSkiaBitmap()
        )
        pP.calculateRatio(displayPictureSize)
        if (payload.corners == null) return
        mHVM.setClicks((payload.corners!!).map {
            Offset(it.first, it.second)
        })
    }

    fun calculateRatio(displayPictureSize: Size) {
        pP.calculateRatio(displayPictureSize)
        this.displayPictureSize = displayPictureSize
    }

    fun rotate(clockwise: Boolean) {
        if (currentPicture.value.isEmpty) return
        pP.rotate(clockwise)
        mHVM.rotate(clockwise)
    }

    fun getRatio(): Float {
        return pP.ratio
    }

    fun reset() {
        if (pictureQueue.isEmpty()) return
        setPicture(pictureQueue[selectedPictureIndex.value])
    }

    fun doAll() {
        crop()
        contrast()
        copy()
    }

    fun crop() {
        pP.crop(mHVM.clicks.value, displayPictureSize)
        mHVM.clear()
    }

    fun contrast() {
        pP.contrast()
    }

    fun copy() {
        pP.copy()
    }
}