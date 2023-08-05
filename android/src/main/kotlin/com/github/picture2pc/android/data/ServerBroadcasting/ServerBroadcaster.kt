package com.github.picture2pc.android.data.ServerBroadcasting

import androidx.compose.runtime.MutableState

interface ServerBroadcaster {
    val servingState: MutableState<Boolean>

    fun start()

    fun stop()
}