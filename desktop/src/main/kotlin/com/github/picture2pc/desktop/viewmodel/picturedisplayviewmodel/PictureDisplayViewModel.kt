package com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.desktop.data.RotationState
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.div
import com.github.picture2pc.desktop.extention.toImage
import com.github.picture2pc.desktop.extention.translate
import com.github.picture2pc.desktop.ui.constants.Settings
import com.github.picture2pc.desktop.ui.interactionhandler.MovementHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PictureDisplayViewModel(
    private val viewModelScope: CoroutineScope,
    private val dataReceiver: DataTransmitter,
    val pP: PicturePreparation,
) {
    private val pictures = dataReceiver.pictures

    //MutableStateFlow because needs to be updated for Screen
    val totalPictures = MutableStateFlow(0)
    val selectedPictureIndex: MutableStateFlow<Int> = MutableStateFlow(0)

    val currentPicture = pP.editedBitmap
    val overlayPicture = pP.overlayBitmap

    val isSelectPicture = mutableStateOf(false)
    val rotationState: MutableState<RotationState> = mutableStateOf(RotationState.ROTATION_0)

    val movementHandler = MovementHandler(rotationState, pP)

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
        pP.setOriginalPicture(payload.picture.toImage().toComposeImageBitmap().asSkiaBitmap())
        pP.clicks.clear()
        println(payload.corners)
        pP.clicks.addAll(payload.corners.map {
            Pair(
                it.first * pP.editedBitmap.value.width,
                it.second * pP.editedBitmap.value.height
            )
        })
        pP.updateEditedBitmap()
    }

    fun calculateOffset(
        rotationState: RotationState
    ): Pair<Float, Float> {
        val ratio = pP.ratio
        val bound = pP.bounds / ratio
        val radius = Settings.ZOOM_DIAMETER.toFloat() / 2

        val currentDP = (movementHandler.currentDragPoint.value / ratio).translate(
            rotationState, bound
        )

        var offsetPair = Pair(
            currentDP.first - (bound.width / 2),
            currentDP.second - (bound.height / 2)
        )

        if (currentDP.first in 0f..radius) {
            offsetPair = Pair(radius - bound.width / 2, offsetPair.second)
        } else if (currentDP.first in bound.width - radius..bound.width) {
            offsetPair = Pair(bound.width / 2 - radius, offsetPair.second)
        }

        if (currentDP.second in 0f..radius) {
            offsetPair = Pair(offsetPair.first, radius - bound.height / 2)
        } else if (currentDP.second in bound.height - radius..bound.height) {
            offsetPair = Pair(offsetPair.first, bound.height / 2 - radius)
        }

        return offsetPair
    }
}