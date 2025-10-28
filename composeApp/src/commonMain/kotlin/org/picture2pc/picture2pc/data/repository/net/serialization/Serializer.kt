package org.picture2pc.picture2pc.data.repository.net.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor

class Serializer {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val format = Cbor {
            encodeDefaults = true
        }
    }
}