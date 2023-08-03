package com.github.picture2pc.common.net.multicast

data class ReceivedMulticastPayload(val payload: MulticastPayload, val senderAddress: String)
