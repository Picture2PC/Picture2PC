package com.github.picture2pc.common.net.multicast


sealed interface MulticastPayload {
    val stringRepresentation: String
}

object MulticastPayloads {
    object ListServers : MulticastPayload {
        internal val regex = "<P2PC\\|LIST-SERVERS>".toRegex()

        override val stringRepresentation = "<P2PC|LIST-SERVERS>"
    }

    class ServerOnline(val deviceName: String) : MulticastPayload {
        internal companion object {
            internal val regex = "<P2PC\\|SERVER-ONLINE\\|NAME:(?<deviceName>.+)>".toRegex()

            internal fun fromString(string: String): ServerOnline {
                val groups = regex.matchEntire(string)!!.groups

                val deviceName = groups["deviceName"]!!.value

                return ServerOnline(deviceName)
            }
        }

        override val stringRepresentation = "<P2PC|SERVER-ONLINE|NAME:$deviceName>"
    }

    private val regexToPayloadFactoryFunctions: Map<Regex, (String) -> MulticastPayload> = mapOf(
        ListServers.regex to { ListServers },
        ServerOnline.regex to ServerOnline::fromString
    )

    internal fun createPayloadFromString(string: String) = regexToPayloadFactoryFunctions
        .filterKeys { it.matches(string) }
        .values
        .firstOrNull()
        ?.invoke(string)
}
