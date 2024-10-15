package com.github.picture2pc.desktop.data.serverpreferences.impl

import kotlinx.serialization.Serializable

@Serializable
data class PreferencesDataClass(var clientName: String, var connectable: Boolean)