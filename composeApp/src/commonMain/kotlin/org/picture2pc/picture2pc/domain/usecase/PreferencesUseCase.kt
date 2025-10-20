package org.picture2pc.picture2pc.domain.usecase

import org.picture2pc.picture2pc.domain.repository.PreferencesRepository

private const val MAX_NAME_LENGTH = 14

class PreferencesUseCase(
    private val preferencesRepository: PreferencesRepository
) {

    val name = preferencesRepository.name
    val connectable = preferencesRepository.connectable

    // Returns false if name is invalid
    suspend fun setName(name: String): Boolean {
        if (nameIsInvalid(name))
            return false
        preferencesRepository.setName(name)
        return true
    }

    suspend fun setConnectable(connectable: Boolean) {
        preferencesRepository.setConnectable(connectable)
    }

    fun nameIsInvalid(name: String) =
        name.isEmpty() || name.isBlank() || name.length > MAX_NAME_LENGTH
}