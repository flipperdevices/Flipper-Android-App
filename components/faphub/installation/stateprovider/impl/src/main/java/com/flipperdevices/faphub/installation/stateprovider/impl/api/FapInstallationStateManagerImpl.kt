package com.flipperdevices.faphub.installation.stateprovider.impl.api

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@ContributesBinding(AppGraph::class, FapInstallationStateManager::class)
class FapInstallationStateManagerImpl @Inject constructor(
    private val fapManifestApi: FapManifestApi,
    private val queueApi: FapInstallationQueueApi
) : FapInstallationStateManager {
    override fun getFapStateFlow(
        scope: CoroutineScope,
        applicationUid: String,
        currentVersion: SemVer
    ) = combine(
        fapManifestApi.getManifestFlow(),
        queueApi.getFlowById(scope, applicationUid)
    ) { manifests, queueState ->
        getState(manifests, applicationUid, queueState, currentVersion)
    }.stateIn(scope, SharingStarted.Eagerly, FapState.NotInitialized)

    @Suppress("UnusedPrivateMember")
    private suspend fun getState(
        manifests: List<FapManifestItem>?,
        applicationUid: String,
        queueState: FapQueueState,
        currentVersion: SemVer
    ): FapState {
        val stateFromQueue = queueStateToFapState(queueState)
        if (stateFromQueue != null) {
            return stateFromQueue
        }

        return if (manifests == null) {
            FapState.RetrievingManifest
        } else if (manifests.find { it.uid == applicationUid } != null) {
            FapState.Installed
        } else {
            FapState.ReadyToInstall
        }
    }

    private fun queueStateToFapState(queueState: FapQueueState) = when (queueState) {
        is FapQueueState.InProgress -> when (queueState.request) {
            is FapActionRequest.Cancel -> FapState.Canceling
            is FapActionRequest.Install -> FapState.InstallationInProgress(queueState.float)
        }

        is FapQueueState.Pending -> when (queueState.request) {
            is FapActionRequest.Cancel -> FapState.Canceling
            is FapActionRequest.Install -> FapState.InstallationInProgress(0f)
        }

        FapQueueState.NotFound,
        is FapQueueState.Failed -> null
    }
}
