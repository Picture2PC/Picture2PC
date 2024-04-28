package com.github.picture2pc.common.net.common

import kotlinx.serialization.Serializable

/*
@Serializable
open class NetworkDataPayload(@Transient val networkDataPayloads: NetworkDataPayloads? = null) {
    public fun emit(transceiver: DefaultDataPayloadTransceiver)
    {
        val stream = ByteArrayOutputStream()
        Json.encodeToStream(serializer(), this, stream)

    }
}*/



@Serializable
open class NetworkDataPayload


class NetworkDataPayloads
