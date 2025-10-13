package com.github.picture2pc.desktop.viewmodel.mainscreen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.common.ui.notification.NotificationHandler
import com.github.picture2pc.common.ui.notification.NotificationMessages
import com.github.picture2pc.desktop.data.RotationState
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.toImage
import com.github.picture2pc.desktop.net.datatransmitter.DataTransmitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private interface QueuedPicture {
    val payloadPicture: TcpPayload.Picture
    var seen: Boolean
}

class PictureDisplayViewModel(
    viewModelScope: CoroutineScope,
    dataReceiver: DataTransmitter,
    private val notificationHandler: NotificationHandler,
    private val mHVM: MovementHandlerViewModel,
    private val pP: PicturePreparation,
) {
    private val picturePayloads = dataReceiver.picture

    private val pictureQueue: ArrayDeque<QueuedPicture> = ArrayDeque()
    val totalPictures = MutableStateFlow(0)
    val selectedPictureIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentPicture = pP.editedBitmap
    val unseenPictures: MutableStateFlow<Int> = MutableStateFlow(0)
    private var displayPictureSize = Size(0f, 0f)
    var isFocused = false

    init {
        picturePayloads.onEach { payload ->
            pictureQueue.addLast(object : QueuedPicture {
                override val payloadPicture = payload
                override var seen = false
            })

            var message = NotificationMessages.PICTURE_SENT
            if (unseenPictures.value + 1 > 0) message += " (${unseenPictures.value + 1} unseen pictures)"
            notificationHandler.displayNotification(
                "Picture2PC",
                message,
                isFocused
            )

            if (pictureQueue.size == 1) {
                setPicture(0)
                pictureQueue[0].seen = true
            }
            totalPictures.value = pictureQueue.size
            unseenPictures.value = pictureQueue.count { picture -> !picture.seen }
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
        setPicture(selectedPictureIndex.value)
        notificationHandler.showNotification.value = false
        mHVM.rotationState.value = RotationState.ROTATION_0
    }

    private fun setPicture(queueIndex: Int) {
        val payloadPicture = pictureQueue[queueIndex].payloadPicture
        pictureQueue[queueIndex].seen = true

        pP.setOriginalPicture(
            payloadPicture.picture.toImage().toComposeImageBitmap().asSkiaBitmap()
        )
        pP.calculateRatio(displayPictureSize)
        if (payloadPicture.corners == null) return
        mHVM.setClicks((payloadPicture.corners!!).map {
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
        setPicture(0)
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