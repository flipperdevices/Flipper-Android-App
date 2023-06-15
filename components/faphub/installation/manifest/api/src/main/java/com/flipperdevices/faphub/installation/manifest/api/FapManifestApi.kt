package com.flipperdevices.faphub.installation.manifest.api

import com.flipperdevices.faphub.installation.manifest.model.FapManifestEnrichedItem
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import kotlinx.coroutines.flow.StateFlow

interface FapManifestApi {
    fun getManifestFlow(): StateFlow<FapManifestState>
    suspend fun add(pathToFap: String, fapManifestItem: FapManifestEnrichedItem)
    suspend fun remove(applicationUid: String)
    fun invalidateAsync()
}
