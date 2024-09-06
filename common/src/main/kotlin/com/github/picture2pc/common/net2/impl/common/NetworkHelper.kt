package com.github.picture2pc.common.net2.impl.common

import java.net.Inet4Address
import java.net.NetworkInterface

class NetworkHelper {
    companion object {
        init {
            System.setProperty("java.net.preferIPv4Stack", "true")
        }

        fun getDefaultNetworkInterface(): NetworkInterface? {
            val possible =
                NetworkInterface.getNetworkInterfaces().asSequence().filter { isPossible(it) }
                    .toSet()
            return possible.maxWithOrNull(compareBy { it.inetAddresses.asSequence().count() })
        }

        private fun isPossible(networkInterface: NetworkInterface): Boolean {
            return (networkInterface.isUp && !networkInterface.isVirtual && !networkInterface.isLoopback
                    && networkInterface.supportsMulticast() && !networkInterface.name.startsWith(
                "vEthernet",
                true
            ) && !networkInterface.isPointToPoint && networkInterface.inetAddresses.asSequence()
                .any { !it.isLoopbackAddress && it::class == Inet4Address::class })
        }
    }
}