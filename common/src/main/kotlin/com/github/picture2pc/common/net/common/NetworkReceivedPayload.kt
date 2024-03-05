package com.github.picture2pc.common.net.common

import java.net.InetAddress

data class NetworkReceivedPayload<T>(val payload: T, val senderAddress: InetAddress){
    val stringAddress:String
        get() = senderAddress.hostAddress
}
