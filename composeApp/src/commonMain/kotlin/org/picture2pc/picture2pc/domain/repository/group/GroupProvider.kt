package org.picture2pc.picture2pc.domain.repository.group

import kotlinx.coroutines.flow.StateFlow


interface GroupProvider {
    val groups: StateFlow<List<PublicGroup>>

    suspend fun add(group: InternalGroup)
}