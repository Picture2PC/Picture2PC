package com.github.picture2pc.common.data.serverpreferences

import kotlinx.coroutines.flow.StateFlow

abstract class ServerPreferencesRepository {
    abstract val preferences: StateFlow<PreferencesDataClass>
    abstract fun savePreferences()
    abstract fun loadPreferences()
}