package com.github.picture2pc.common.net.data.packet

import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.common.net.data.serialization.Convertible
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
data class Packet(val type: KClass<out Payload>, val len: Int, val sourcePeer: Peer) : Convertible