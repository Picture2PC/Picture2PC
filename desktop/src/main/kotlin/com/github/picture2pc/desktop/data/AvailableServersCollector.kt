package com.github.picture2pc.desktop.data

import kotlinx.coroutines.flow.Flow

interface AvailableServersCollector {
    val availableServers: Flow<Server>
    fun requestServers()

    data class Server(val deviceName: String, val senderAddress: String)
}
