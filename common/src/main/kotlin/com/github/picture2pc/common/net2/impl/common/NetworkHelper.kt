package com.github.picture2pc.common.net2.impl.common

import java.net.InetAddress
import java.net.NetworkInterface

class NetworkHelper {
    companion object {
        fun getDefaultNetworkInterface(): NetworkInterface? {
            val hostName = InetAddress.getLocalHost()
            val networkInterface = NetworkInterface.getByInetAddress(hostName)
            if (isPossible(networkInterface))
                return networkInterface
            val possible =
                NetworkInterface.getNetworkInterfaces().asSequence().filter { isPossible(it) }
                    .toSet()
            return possible.maxWithOrNull(compareBy { it.inetAddresses.asSequence().count() })
        }

        private fun isPossible(networkInterface: NetworkInterface): Boolean {
            return (networkInterface.isUp && !networkInterface.isVirtual && !networkInterface.isLoopback && !networkInterface.name.startsWith(
                "vEthernet",
                true
            ) && networkInterface.supportsMulticast())
        }
    }
}