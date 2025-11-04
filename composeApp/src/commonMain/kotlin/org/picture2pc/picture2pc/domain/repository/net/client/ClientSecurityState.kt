package org.picture2pc.picture2pc.domain.repository.net.client

import androidx.compose.ui.graphics.Color
import org.picture2pc.picture2pc.data.repository.net.peer.Peer
import org.picture2pc.picture2pc.domain.repository.SharedKey
import org.picture2pc.picture2pc.presentation.elements.Style

sealed class ClientSecurityState(val color: Color, val displayText: String) {
    object PeerUnknown : ClientSecurityState(Style.Colors.States.CONNECTED, "")
    sealed class PeerKnown(val peer: Peer, color: Color, displayText: String) :
        ClientSecurityState(color, displayText) {
        class Encrypted(peer: Peer, val sharedKey: SharedKey) : PeerKnown(peer, Style.Colors.States.CONNECTED, "")
        class UnVerified(peer: Peer) : PeerKnown(peer, Style.Colors.States.CONNECTED, "")
    }
}