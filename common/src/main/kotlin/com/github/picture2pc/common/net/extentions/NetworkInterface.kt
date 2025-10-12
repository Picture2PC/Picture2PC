package com.github.picture2pc.common.net.extentions

import java.net.Inet4Address
import java.net.NetworkInterface

fun getDefaultNetworkInterfaces(): Sequence<NetworkInterface> {
    val possible = NetworkInterface.getNetworkInterfaces().asSequence().filter(::isPossible)
    return possible
}

private fun isPossible(networkInterface: NetworkInterface): Boolean {
    return (networkInterface.isUp && !networkInterface.isVirtual && !networkInterface.isLoopback
            && networkInterface.supportsMulticast() && !networkInterface.name.startsWith(
        "vEthernet",
        true
    ) && !networkInterface.isPointToPoint && networkInterface.inetAddresses.asSequence()
        .any { !it.isLoopbackAddress && it::class == Inet4Address::class })
}
