package com.github.picture2pc.android.net.serveronlinenotifier.impl

import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.android.net.serveronlinenotifier.ServerOnlineNotifier
import com.github.picture2pc.common.net.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.common.net.multicast.MulticastPayloads
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

    private var loadedName = false

    init {
        multicastPaylaodTransceiver.incomingPayloads
            .onEach { (payload, _) ->
                if (payload is MulticastPayloads.ListServers && serverConnectable.value) {

                    launch {
                        emitServerOnline(serverName.value)
                    }
                }
            }.launchIn(this)

        launch {

            serverName.onEach {
                if (serverConnectable.value && loadedName)
                    emitServerOnline(it)
                if (it != "<LOADING>")
                    loadedName = true

            }.launchIn(this)


            serverConnectable.onEach {
                if (it)
                    emitServerOnline(serverName.value)
            }.launchIn(this)
        }
    }

    private suspend fun emitServerOnline(serverName: String) {
        multicastPaylaodTransceiver.outgoingPayloads.emit(MulticastPayloads.ServerOnline(serverName))
    }
}