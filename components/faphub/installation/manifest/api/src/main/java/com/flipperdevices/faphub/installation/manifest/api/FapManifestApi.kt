package com.flipperdevices.faphub.installation.manifest.api

import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import kotlinx.coroutines.flow.StateFlow

interface FapManifestApi {
    fun getManifestFlow(): StateFlow<List<FapManifestItem>?>
    suspend fun add(pathToFap: String, fapManifestItem: FapManifestItem)
    fun invalidateAsync()
}
