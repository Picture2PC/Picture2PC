package com.github.picture2pc.desktop.net.datatransmitter.impl

import com.github.picture2pc.common.net2.impl.tcp.TcpPayloadTransceiver
import com.github.picture2pc.common.net2.payloads.TcpPayload
import com.github.picture2pc.desktop.extention.toImage
import com.github.picture2pc.desktop.net.datatransmitter.DataReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.skia.Image
import kotlin.coroutines.CoroutineContext

class TcpPictureReceiver(
    tcpPayloadTransceiver: TcpPayloadTransceiver,
    override val coroutineContext: CoroutineContext,
): DataReceiver, CoroutineScope {
    private val _pictures = MutableSharedFlow<Image>(replay = 5)
    override val pictures = _pictures.asSharedFlow()
    init {
        tcpPayloadTransceiver.receivedPayloads.onEach {
            when (it) {
                is TcpPayload.Picture -> { _pictures.emit(it.picture.toImage()) }
                else -> {}
            }
        }.launchIn(this)
    }
    override suspend fun addPicture(image: Image) {
        _pictures.emit(image)
    }
}