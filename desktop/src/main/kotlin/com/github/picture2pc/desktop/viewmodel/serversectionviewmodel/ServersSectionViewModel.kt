package com.github.picture2pc.desktop.viewmodel.serversectionviewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.github.picture2pc.common.net2.impl.tcp.ClientState
import com.github.picture2pc.desktop.data.availableserverscollector.AvailableServersCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

class ServersSectionViewModel(
    private val availableServersCollector: AvailableServersCollector,
    override val coroutineContext: CoroutineContext,
) : CoroutineScope {

    private val _serverEntries = MutableStateFlow(setOf<ServerEntryState>())
    val serverEntries = _serverEntries.asStateFlow()

    init {
        availableServersCollector.availableServers
            .onEach {

                if (_serverEntries.value.any { s -> s.deviceAddress == it.senderAddress }) {
                    _serverEntries.value.first { s -> s.deviceAddress == it.senderAddress }.deviceName.value =
                        it.deviceName
                    return@onEach
                }
                _serverEntries.value += ServerEntryState(
                    mutableStateOf(it.deviceName),
                    it.senderAddress,
                    ClientState.CONNECTED //TODO: Replace with actual received connection state
                )
            }
            .launchIn(this)
    }

    fun refreshServers() {
        _serverEntries.value = setOf()
        availableServersCollector.requestServers()
    }

    data class ServerEntryState(
        val deviceName: MutableState<String>,
        val deviceAddress: String,
        val connectionState: ClientState
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as ServerEntryState
            return deviceAddress == other.deviceAddress
        }

        override fun hashCode(): Int {
            return deviceAddress.hashCode()
        }
    }
}
