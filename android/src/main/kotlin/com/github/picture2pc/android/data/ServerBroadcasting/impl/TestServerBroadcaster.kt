package com.github.picture2pc.android.data.ServerBroadcasting.impl

import com.github.picture2pc.android.data.Device.Device
import com.github.picture2pc.android.data.ServerBroadcasting.ServerBroadcaster
import kotlin.coroutines.CoroutineContext

class TestServerBroadcaster(val corutineScope: CoroutineContext, override val device: Device) : ServerBroadcaster {

}