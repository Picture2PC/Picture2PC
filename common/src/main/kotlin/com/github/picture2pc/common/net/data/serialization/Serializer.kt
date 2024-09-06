package com.github.picture2pc.common.net.data.serialization

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