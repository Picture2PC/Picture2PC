package org.picture2pc.picture2pc.domain.repository.net.client

import androidx.compose.ui.graphics.Color
import org.picture2pc.picture2pc.data.repository.net.peer.Peer
import org.picture2pc.picture2pc.ui.theme.ConnectionColors

sealed class ClientSecurityState(val color: Color, val displayText: String) {
    object PeerUnknown : ClientSecurityState(ConnectionColors.Pending, "")
    sealed class PeerKnown(val peer: Peer, color: Color, displayText: String) :
        ClientSecurityState(color, displayText) {
        class Verified(peer: Peer) : PeerKnown(peer, ConnectionColors.Connected, "")
        class UnVerified(peer: Peer) : PeerKnown(peer, ConnectionColors.Disconnected, "")
    }
}