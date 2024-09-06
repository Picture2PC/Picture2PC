package com.github.picture2pc.android.net.datatransmitter.impl

import android.graphics.Bitmap
import com.github.picture2pc.android.extentions.toByteArray
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.common.net.impl.tcp.TcpPayloadTransceiver
import com.github.picture2pc.common.net.payloads.TcpPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TcpPictureTransmitter(
    private val tcpPayloadTransceiver: TcpPayloadTransceiver,
    override val coroutineContext: CoroutineContext,
): DataTransmitter, CoroutineScope {
    override fun send(picture: Bitmap) {
        val payload = TcpPayload.Picture(picture.toByteArray())
        launch { tcpPayloadTransceiver.sendPayload(payload) }
    }
}