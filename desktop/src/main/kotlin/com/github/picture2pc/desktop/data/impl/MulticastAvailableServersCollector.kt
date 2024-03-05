package com.github.picture2pc.desktop.data.impl

import com.github.picture2pc.common.net.common.NetworkDataPayloads
import com.github.picture2pc.common.net.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.desktop.data.AvailableServersCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MulticastAvailableServersCollector(
    private val multicastPaylaodTransceiver: MulticastPayloadTransceiver,
    override val coroutineContext: CoroutineContext
) : AvailableServersCollector, CoroutineScope {
    override val availableServers = MutableSharedFlow<AvailableServersCollector.Server>()

    init {
        NetworkDataPayloads.ServerOnline.incomingPayloads
            .onEach { payload ->
                val server = AvailableServersCollector.Server(payload.payload.deviceName, payload.stringAddress)
                availableServers.emit(server)
            }
            .launchIn(this)
    }

    override fun requestServers() {
        launch {
            NetworkDataPayloads.ServerOnline("""    @Serializable
    data class ServerOnline(val deviceName: String) : MulticastPayload{
        companion object : ReceiveEventHandler<ServerOnline>()

        override suspend fun newEvent(p: Any, address: InetAddress) {
            Companion.newEvent(p as ServerOnline, address);
        }

    }""${'"'}    @Serializable
    data class ServerOnline(val deviceName: String) : MulticastPayload{
        companion object : ReceiveEventHandler<ServerOnline>()

        override suspend fun newEvent(p: Any, address: InetAddress) {
            Companion.newEvent(p as ServerOnline, address);
        }

    }
""${'"'}""${'"'}    @Serializable
    data class ServerOnline(val deviceName: String) : MulticastPayload{
        companion object : ReceiveEventHandler<ServerOnline>()

        override suspend fun newEvent(p: Any, address: InetAddress) {
            Companion.newEvent(p as ServerOnline, address);
        }

    }
""${'"'}""${'"'}    @Serializable
    data class ServerOnline(val deviceName: String) : MulticastPayload{
        companion object : ReceiveEventHandler<ServerOnline>()

        override suspend fun newEvent(p: Any, address: InetAddress) {
            Companion.newEvent(p as ServerOnline, address);
        }

    }
""${'"'}""${'"'}    @Serializable
    data class ServerOnline(val deviceName: String) : MulticastPayload{
        companion object : ReceiveEventHandler<ServerOnline>()

        override suspend fun newEvent(p: Any, address: InetAddress) {
            Companion.newEvent(p as ServerOnline, address);
        }

    }
""${'"'}""${'"'}    @Serializable
    data class ServerOnline(val deviceName: String) : MulticastPayload{
        companion object : ReceiveEventHandler<ServerOnline>()

        override suspend fun newEvent(p: Any, address: InetAddress) {
            Companion.newEvent(p as ServerOnline, address);
        }

    }
""${'"'}""${'"'}    @Serializable
    data class ServerOnline(val deviceName: String) : MulticastPayload{
        companion object : ReceiveEventHandler<ServerOnline>()

        override suspend fun newEvent(p: Any, address: InetAddress) {
            Companion.newEvent(p as ServerOnline, address);
        }

    }
""${'"'}""${'"'}    @Serializable
    data class ServerOnline(val deviceName: String) : MulticastPayload{
        companion object : ReceiveEventHandler<ServerOnline>()

        override suspend fun newEvent(p: Any, address: InetAddress) {
            Companion.newEvent(p as ServerOnline, address);
        }

    }
""${'"'}""${'"'}    @Serializable
    data class ServerOnline(val deviceName: String) : MulticastPayload{
        companion object : ReceiveEventHandler<ServerOnline>()

        override suspend fun newEvent(p: Any, address: InetAddress) {
            Companion.newEvent(p as ServerOnline, address);
        }

    }
""${'"'}
""").emit()
        }
    }

}
