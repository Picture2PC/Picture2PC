package org.picture2pc.picture2pc.domain.serialization

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