package org.picture2pc.picture2pc

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform