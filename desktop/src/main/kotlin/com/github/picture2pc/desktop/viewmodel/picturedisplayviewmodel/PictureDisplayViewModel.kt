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
                it.first * pP.displayPictureSize.width,
                it.second * pP.displayPictureSize.height
            )
        }.forEach { movementHandler.addClick(it) }
        pP.updateEditedBitmap()
    }

    fun calculateOffset(rotationState: RotationState): Offset {
        val ratio = pP.ratio
        val bound = pP.bounds / ratio
        val radius = Settings.ZOOM_DIAMETER.toFloat() / 2

        val currentDP = (movementHandler.dragPoint / ratio).translate(
            rotationState, bound
        )

        var offsetPair = Offset(
            currentDP.x - (bound.width / 2),
            currentDP.y - (bound.height / 2)
        )

        if (currentDP.x in 0f..radius) {
            offsetPair = Offset(radius - bound.width / 2, offsetPair.y)
        } else if (currentDP.x in bound.width - radius..bound.width) {
            offsetPair = Offset(bound.width / 2 - radius, offsetPair.y)
        }

        if (currentDP.y in 0f..radius) {
            offsetPair = Offset(offsetPair.x, radius - bound.height / 2)
        } else if (currentDP.y in bound.height - radius..bound.height) {
            offsetPair = Offset(offsetPair.x, bound.height / 2 - radius)
        }

        return offsetPair
    }
}