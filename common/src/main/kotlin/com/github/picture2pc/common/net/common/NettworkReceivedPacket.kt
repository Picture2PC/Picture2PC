package com.github.picture2pc.common.net.common

import java.io.InputStream
import java.net.InetAddress

internal data class ReceivedMulticastPacket(val content: InputStream, val address: InetAddress){

}