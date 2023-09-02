package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClientsViewModel {
    private val _serverEntries = MutableStateFlow(listOf<ClientData>())
    val serverEntries = _serverEntries.asStateFlow()

    data class ClientData(
        val name: String,
        val address: String,
    ) {
        override fun hashCode(): Int {
            return address.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

    }
}