package com.github.picture2pc.android.net.datatransmitter.impl

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.picture2pc.android.extentions.toBase64
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.common.net2.impl.tcp.TcpPayloadTransceiver
import com.github.picture2pc.common.net2.payloads.TcpPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TcpPictureTransmitter(
    private val tcpPayloadTransceiver: TcpPayloadTransceiver,
    override val coroutineContext: CoroutineContext,
): DataTransmitter, CoroutineScope {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun send(picture: Bitmap) {
        val payload = TcpPayload.Picture(picture.toBase64())
        launch { tcpPayloadTransceiver.sendPayload(payload) }
    }
}