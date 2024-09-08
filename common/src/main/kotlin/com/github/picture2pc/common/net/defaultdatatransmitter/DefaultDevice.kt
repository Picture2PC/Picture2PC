package com.github.picture2pc.android.net.datatransmitter

import com.github.picture2pc.common.net.data.client.ClientState
import kotlinx.coroutines.flow.StateFlow

open class DefaultDevice(val name: StateFlow<String>, val deviceState: StateFlow<ClientState>)