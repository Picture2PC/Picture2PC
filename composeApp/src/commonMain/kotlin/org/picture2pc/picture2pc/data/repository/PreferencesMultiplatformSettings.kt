package org.picture2pc.picture2pc.data.repository

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanStateFlow
import com.russhwolf.settings.coroutines.getStringStateFlow
import kotlinx.coroutines.CoroutineScope
import org.picture2pc.picture2pc.domain.repository.PreferencesRepository

@OptIn(ExperimentalSettingsApi::class)
class PreferencesMultiplatformSettings(
    private val observableSettings: ObservableSettings,
    defaultCoroutineScope: CoroutineScope
) : PreferencesRepository {
    private companion object {
        const val NAME_KEY = "name"
        const val CONNECTABLE_KEY = "connectable"
        const val PRIVATEKEY_KEY = "privateKey"
        const val PUBLICKEY_KEY = "publicKey"
    }

    override var name =
        observableSettings.getStringStateFlow(defaultCoroutineScope, NAME_KEY, "")
    override var connectable = observableSettings.getBooleanStateFlow(
        defaultCoroutineScope,
        CONNECTABLE_KEY, false
    )

    override val privateKey = observableSettings.getStringStateFlow(defaultCoroutineScope, PRIVATEKEY_KEY, "")
    override val publicKey = observableSettings.getStringStateFlow(defaultCoroutineScope, PUBLICKEY_KEY, "")

    override suspend fun setPublicKey(publicKey: String) {
        observableSettings.putString(PUBLICKEY_KEY, publicKey)
    }

    override suspend fun setPrivateKey(privateKey: String) {
        observableSettings.putString(PRIVATEKEY_KEY, privateKey)
    }

    override suspend fun setName(name: String) {
        observableSettings.putString(NAME_KEY, name)
    }

    override suspend fun setConnectable(connectable: Boolean) {
        observableSettings.putBoolean(CONNECTABLE_KEY, connectable)
    }
}