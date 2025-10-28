package org.picture2pc.picture2pc.data.repository.net.payload

import io.ktor.util.network.NetworkAddress


data class PayloadInfo(val senderInetSocketAddress: NetworkAddress? = null)

