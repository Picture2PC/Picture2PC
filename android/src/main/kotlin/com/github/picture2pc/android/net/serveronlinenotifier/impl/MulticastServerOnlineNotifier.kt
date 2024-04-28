package com.github.picture2pc.android.net.serveronlinenotifier.impl

import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.android.net.serveronlinenotifier.ServerOnlineNotifier
import com.github.picture2pc.common.net2.NetworkPayloadTransceiver
import com.github.picture2pc.common.net2.impl.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.common.net2.payloads.ListPeers
import com.github.picture2pc.common.net2.payloads.PeerOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MulticastServerOnlineNotifier(
    private val multicastPaylaodTransceiver: MulticastPayloadTransceiver,
    override val coroutineContext: CoroutineContext,
    override val serverPreferencesRepository: ServerPreferencesRepository
) : ServerOnlineNotifier, CoroutineScope {
    private val serverName = serverPreferencesRepository.name.stateIn(
        scope = this,
        started = SharingStarted.Eagerly,
        initialValue = "<LOADING>"
    )

    private val serverConnectable = serverPreferencesRepository.connectable.stateIn(
        scope = this,
        started = SharingStarted.Lazily,
        initialValue = false
    )

    init {
        multicastPaylaodTransceiver.receivedPayloads.onEach {
            (it as? ListPeers)?.let {
                if (serverConnectable.value)
                    emitServerOnline(serverName.value)
            }
        }.launchIn(this)

        launch {
            serverName.onEach {
                if (serverConnectable.value)
                    emitServerOnline(it)

            }.launchIn(this)

            serverConnectable.onEach {
                if (it)
                    emitServerOnline(serverName.value)

            }.launchIn(this)
        }
    }

    private suspend fun emitServerOnline(serverName: String) {
        NetworkPayloadTransceiver.name = serverName
        multicastPaylaodTransceiver.sendPayload(PeerOnline())
    }
}