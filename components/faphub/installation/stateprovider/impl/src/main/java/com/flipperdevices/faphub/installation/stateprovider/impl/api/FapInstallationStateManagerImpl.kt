package com.flipperdevices.faphub.installation.stateprovider.impl.api

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapInstallationStateManager::class)
class FapInstallationStateManagerImpl @Inject constructor(
    private val fapManifestApi: FapManifestApi
) : FapInstallationStateManager {
    override fun getFapStateFlow(
        scope: CoroutineScope,
        applicationId: String,
        currentVersion: SemVer
    ) = fapManifestApi.getManifestFlow().map { manifests ->
        getState(manifests, applicationId, currentVersion)
    }.stateIn(scope, SharingStarted.Eagerly, FapState.NotInitialized)

    @Suppress("UnusedPrivateMember")
    private suspend fun getState(
        manifests: List<FapManifestItem>?,
        applicationId: String,
        currentVersion: SemVer
    ): FapState {
        return if (manifests == null) {
            FapState.RetrievingManifest
        } else if (manifests.find { it.applicationId == applicationId } != null) {
            FapState.Installed
        } else {
            FapState.ReadyToInstall
        }
    }
}
