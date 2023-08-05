package com.github.picture2pc.android.data.ServerBroadcasting.impl

import com.github.picture2pc.android.data.Device.Device
import com.github.picture2pc.android.data.ServerBroadcasting.ServerBroadcaster
import kotlin.coroutines.CoroutineContext

class TestServerBroadcaster(val corutineScope: CoroutineContext, val device: Device) : ServerBroadcaster {
    override val servingState = device.serving

    override fun start() {
        servingState.value = true
    }

    override fun stop() {
        servingState.value = false
    }
}