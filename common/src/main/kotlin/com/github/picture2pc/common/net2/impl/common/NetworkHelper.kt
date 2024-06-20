package com.github.picture2pc.common.net2.impl.common

import java.net.InetAddress
import java.net.NetworkInterface

class NetworkHelper {
    companion object {
        fun getDefaultNetworkInterface(): NetworkInterface? {
            val hostName = InetAddress.getLocalHost()
            val networkInterface = NetworkInterface.getByInetAddress(hostName)
            val possible = NetworkInterface.getNetworkInterfaces().asSequence().filter { i ->
                i.isUp && !i.isVirtual && !i.isLoopback && !i.name.startsWith(
                    "vEthernet",
                    true
                ) && i.supportsMulticast()
            }.toSet()
            if (networkInterface in possible)
                return networkInterface
            return possible.maxWithOrNull(compareBy { it.inetAddresses.asSequence().count() })
        }
    }
}