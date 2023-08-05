package com.github.picture2pc.desktop.viewmodel

import com.github.picture2pc.desktop.data.AvailableServersCollector
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
                _serverEntries.value = _serverEntries.value + ServerEntryState(
                    it.deviceName,
                    it.senderAddress,
                    ServerEntryConnectionState.DISCONNECTED //TODO
                )
            }
            .launchIn(this)
    }

    fun refreshServers() {
        _serverEntries.value = setOf()
        availableServersCollector.requestServers()
    }

    data class ServerEntryState(
        val deviceName: String,
        val deviceAddress: String,
        val connectionState: ServerEntryConnectionState
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as ServerEntryState
            if (deviceAddress != other.deviceAddress) return false
            return true
        }

        override fun hashCode(): Int {
            return deviceAddress.hashCode()
        }
    }

    enum class ServerEntryConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

}
