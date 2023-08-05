package com.github.picture2pc.android.data.ServerBroadcasting.impl

import com.github.picture2pc.android.data.Device.Device
import com.github.picture2pc.android.data.ServerBroadcasting.ServerBroadcaster
import com.github.picture2pc.common.net.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.common.net.multicast.MulticastPayloads
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MulticastServerBroadcaster(private val multicastPaylaodTransceiver: MulticastPayloadTransceiver, override val coroutineContext: CoroutineContext, override val device: Device) : ServerBroadcaster, CoroutineScope {
    val servingState = device.serving

    init {
        multicastPaylaodTransceiver.incomingPayloads
        .onEach { (payload, senderAddress) ->
            if (payload is MulticastPayloads.ListServers && servingState.value) {

                launch{
                    multicastPaylaodTransceiver.outgoingPayloads.emit(MulticastPayloads.ServerOnline(device.name.value))
                }
            }
        }.launchIn(this)
        device.name.onEach{
            if (servingState.value)
                multicastPaylaodTransceiver.outgoingPayloads.emit(MulticastPayloads.ServerOnline(it))
        }.launchIn(this)
    }


}