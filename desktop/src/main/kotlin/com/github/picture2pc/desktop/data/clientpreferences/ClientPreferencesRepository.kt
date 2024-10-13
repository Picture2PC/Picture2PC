package com.github.picture2pc.desktop.data.clientpreferences

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class ClientPreferencesRepository(private val file: File) {
    private val _clientName = MutableStateFlow("")

    val clientName: StateFlow<String> = _clientName

    init {
        loadClientName()
    }

    fun setClientName(clientName: String) {
        _clientName.value = clientName
        file.writeText(clientName)
    }

    fun loadClientName() {
        if (file.exists()) {
            _clientName.value = file.readText()
        }
    }
}