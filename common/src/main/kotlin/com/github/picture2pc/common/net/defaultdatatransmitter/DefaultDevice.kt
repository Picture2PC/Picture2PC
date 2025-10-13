package com.github.picture2pc.common.net.defaultdatatransmitter

import com.github.picture2pc.common.net.data.client.ClientState
import kotlinx.coroutines.flow.StateFlow

open class DefaultDevice(
    val uuid: String,
    val name: StateFlow<String>, 
    val deviceState: StateFlow<ClientState>,
    val canReceive: StateFlow<Boolean>,
    val groupVerified: StateFlow<Boolean>
)