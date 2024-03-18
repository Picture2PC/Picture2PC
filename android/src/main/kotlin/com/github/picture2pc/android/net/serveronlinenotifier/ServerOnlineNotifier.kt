package com.github.picture2pc.android.net.serveronlinenotifier

import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository

interface ServerOnlineNotifier {
    val serverPreferencesRepository: ServerPreferencesRepository
}