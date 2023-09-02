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
    private val name = serverPreferencesRepository.name.stateIn(
        scope = this,
        started = SharingStarted.Eagerly,
        initialValue = "<LOADING>"
    )

    private val servingState = serverPreferencesRepository.connectable.stateIn(
        scope = this,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    init {
        multicastPaylaodTransceiver.incomingPayloads
            .onEach { (payload, _) ->
                if (payload is MulticastPayloads.ListServers && servingState.value) {

                    launch {
                        multicastPaylaodTransceiver.outgoingPayloads.emit(
                            MulticastPayloads.ServerOnline(
                                name.value
                            )
                        )
                    }
                }
            }.launchIn(this)
        serverPreferencesRepository.name.onEach {
            if (servingState.value)
                multicastPaylaodTransceiver.outgoingPayloads.emit(MulticastPayloads.ServerOnline(it))
        }.launchIn(this)
    }


}