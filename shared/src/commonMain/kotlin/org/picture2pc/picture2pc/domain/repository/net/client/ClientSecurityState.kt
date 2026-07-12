package org.picture2pc.picture2pc.domain.repository.net.client

import androidx.compose.ui.graphics.Color
import org.picture2pc.picture2pc.data.repository.net.common.peer.Peer
import org.picture2pc.picture2pc.domain.repository.group.Room
import org.picture2pc.picture2pc.domain.repository.net.encryption.SharedKey
import org.picture2pc.picture2pc.ui.theme.ConnectionColors

sealed class ClientSecurityState(val color: Color, val displayText: String) {
    object PeerUnknown : ClientSecurityState(ConnectionColors.Pending, "")


    sealed class PeerKnown(val peer: Peer, color: Color, displayText: String) :
        ClientSecurityState(color, displayText) {
        interface InRoom {
            val room: Room
        }

        abstract fun addRoom(room: Room): PeerKnown
        abstract fun removeRoom(): PeerKnown

        open class Encrypted(peer: Peer, val sharedKey: SharedKey) : PeerKnown(peer, ConnectionColors.Connected, "") {
            class InRoom(override val room: Room, peer: Peer, sharedKey: SharedKey) : PeerKnown.InRoom,
                Encrypted(peer, sharedKey)

            override fun addRoom(room: Room): InRoom {
                return InRoom(room, peer, sharedKey)
            }

            override fun removeRoom(): Encrypted {
                return Encrypted(peer, sharedKey)
            }
        }

        open class UnEncrypted(peer: Peer) : PeerKnown(peer, ConnectionColors.Disconnected, "") {
            class InRoom(override val room: Room, peer: Peer) : PeerKnown.InRoom, UnEncrypted(peer) {
                override fun addEncryption(sharedKey: SharedKey): Encrypted.InRoom {
                    return Encrypted.InRoom(room, peer, sharedKey)
                }
            }

            override fun addRoom(room: Room): InRoom {
                return InRoom(room, peer)
            }

            override fun removeRoom(): UnEncrypted {
                return UnEncrypted(peer)
            }

            open fun addEncryption(sharedKey: SharedKey): Encrypted {
                return Encrypted(peer, sharedKey)
            }
        }
    }
}