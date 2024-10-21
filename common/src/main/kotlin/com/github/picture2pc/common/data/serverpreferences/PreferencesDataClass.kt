package com.github.picture2pc.common.data.serverpreferences

import kotlinx.serialization.Serializable

@Serializable
data class PreferencesDataClass(var name: String, var connectable: Boolean)