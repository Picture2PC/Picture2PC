package com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.desktop.data.RotationState
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.toImage
import com.github.picture2pc.desktop.ui.interactionhandler.MovementHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PictureDisplayViewModel(
    viewModelScope: CoroutineScope,
    dataReceiver: DataTransmitter,
    val pP: PicturePreparation,
) {
    private val pictures = dataReceiver.pictures
    val totalPictures = MutableStateFlow(0)
    val selectedPictureIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentPicture = pP.editedBitmap
    val rotationState: MutableState<RotationState> =
        mutableStateOf(RotationState.ROTATION_0)
    val movementHandler = MovementHandler()

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
        movementHandler.clear()
        (payload.corners ?: return).map {
            Offset(
                (it.first - 0.5f) * pP.displayPictureSize.width,
                (it.second - 0.5f) * pP.displayPictureSize.height
            )
        }.forEach {
            movementHandler.addClick(it, rotationState.value, pP.displayPictureSize)
        }
    }
}