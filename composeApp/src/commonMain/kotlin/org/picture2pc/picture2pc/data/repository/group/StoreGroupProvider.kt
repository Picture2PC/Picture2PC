package org.picture2pc.picture2pc.data.repository.group

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import org.picture2pc.picture2pc.domain.repository.PreferencesRepository
import org.picture2pc.picture2pc.domain.repository.group.GroupProvider
import org.picture2pc.picture2pc.domain.repository.group.InternalGroup
import org.picture2pc.picture2pc.domain.repository.net.encryption.EncryptionProvider
import org.picture2pc.picture2pc.domain.serialization.asGroupList
import org.picture2pc.picture2pc.domain.serialization.asString

class StoreGroupProvider(
    private val preferencesRepository: PreferencesRepository,
    private val encryptionProvider: EncryptionProvider,
    private val backgroundScope: CoroutineScope
) : GroupProvider {
    override val groups = preferencesRepository.groups.map { it.asGroupList() }
        .stateIn(backgroundScope, SharingStarted.Lazily, emptyList())

    init {
        merge(groups, encryptionProvider.publicKeyString).onEach {
            recomputeGroups()
        }.launchIn(backgroundScope)
    }

    override suspend fun add(group: InternalGroup) {
        preferencesRepository.setGroupData((groups.value + group).asString())
    }

    private suspend fun recomputeGroups() {
        groups.value.forEach {
            it.combinedUuid = encryptionProvider.hashString(it.uuid + encryptionProvider.publicKeyString)
        }
    }
}