package com.github.picture2pc.common.net.data.peer

import com.github.picture2pc.common.data.preferences.PreferencesRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

@Serializable
open class Peer {
    val uuid: String
    val isAny: Boolean

    constructor(uuid: String, isAny: Boolean) {
        this.uuid = uuid
        this.isAny = isAny
    }

    companion object : KoinComponent {
        fun any(): Peer {
            return Peer("0", true)
        }

        private val preferencesRepository: PreferencesRepository by inject()
        private var cachedUuid: String? = null
        private var isInitializing = false

        @Synchronized
        private fun getOrCreateUuid(): String {
            // Return cached value if available
            if (cachedUuid != null) return cachedUuid!!
            
            // Prevent concurrent initialization
            if (isInitializing) {
                // Wait a bit and retry if another thread is initializing
                Thread.sleep(10)
                return getOrCreateUuid()
            }
            
            isInitializing = true
            
            try {
                val storedUuid = preferencesRepository.deviceUuid.value
                cachedUuid = if (storedUuid.isEmpty()) {
                    val newUuid = UUID.randomUUID().toString()
                    // Save the new UUID asynchronously
                    GlobalScope.launch {
                        preferencesRepository.setDeviceUuid(newUuid)
                    }
                    newUuid
                } else {
                    storedUuid
                }
                return cachedUuid!!
            } finally {
                isInitializing = false
            }
        }

        fun getSelf(): Peer {
            return Peer(getOrCreateUuid(), false)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Peer) return false

        if (uuid == other.uuid) return true
        return false
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}