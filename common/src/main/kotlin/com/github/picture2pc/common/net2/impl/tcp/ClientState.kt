package com.github.picture2pc.common.net2.impl.tcp


enum class ClientState {
    PENDING, // has to verify the connection
    CONNECTED,
    WAITINGFORDATA,
    RECEIVING,
    ERRORWHILERECEIVING,
    ERRORWHILESENDING,
    DISCONNECTED
}

