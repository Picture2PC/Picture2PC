package com.github.picture2pc.desktop.viewmodel.mainscreen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.toImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PictureDisplayViewModel(
    viewModelScope: CoroutineScope,
    dataReceiver: DataTransmitter,
    private val movementHandlerViewModel: MovementHandlerViewModel,
    private val pP: PicturePreparation,
) {
    private val pictures = dataReceiver.pictures
    val totalPictures = MutableStateFlow(0)
    val selectedPictureIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentPicture = pP.editedBitmap
    var displayPictureSize = Size(0f, 0f)

    init {
        pictures.onEach {
            if (totalPictures.value == 0)
                setPicture(it)
            totalPictures.value = pictures.replayCache.size
        }.launchIn(viewModelScope)
    }


    fun adjustCurrentPictureIndex(amount: Int) {
        if (pictures.replayCache.isEmpty()) return
        val newIndex = selectedPictureIndex.value + amount
        if (newIndex < 0 || newIndex > pictures.replayCache.size - 1) return

        selectedPictureIndex.value = newIndex
        setPicture(pictures.replayCache[newIndex])
    }

    private fun setPicture(payload: TcpPayload.Picture) {
        pP.setOriginalPicture(
            payload.picture.toImage().toComposeImageBitmap().asSkiaBitmap()
        )
        if (payload.corners == null) return
        movementHandlerViewModel.clear()
        movementHandlerViewModel.setClicks((payload.corners ?: return).map {
            Offset(it.first - 0.5f, it.second - 0.5f)
        })
    }

    fun calculateRatio(displayPictureSize: Size) {
        pP.calculateRatio(displayPictureSize)
        this.displayPictureSize = displayPictureSize
    }

    fun reset() {
        movementHandlerViewModel.clear()
        setPicture(pictures.replayCache[selectedPictureIndex.value])
    }

    fun doAll() {
        crop()
        contrast()
        copy()
    }

    fun crop() {
        pP.crop(movementHandlerViewModel.clicks.value, displayPictureSize)
    }

    fun contrast() {
        pP.contrast()
    }

    fun copy() {
        pP.copy()
    }
}