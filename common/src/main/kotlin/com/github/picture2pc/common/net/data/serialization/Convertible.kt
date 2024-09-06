package com.github.picture2pc.common.net.data.serialization


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer

interface Convertible {
    @OptIn(ExperimentalSerializationApi::class)
    fun asByteArray(): ByteArray {
        return Serializer.format.encodeToByteArray(serializer(), this)
    }
}