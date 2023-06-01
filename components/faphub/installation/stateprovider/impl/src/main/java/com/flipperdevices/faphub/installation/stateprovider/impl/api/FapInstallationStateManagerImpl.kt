package com.flipperdevices.faphub.installation.stateprovider.impl.api

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.combine

@ContributesBinding(AppGraph::class, FapInstallationStateManager::class)
class FapInstallationStateManagerImpl @Inject constructor(
    private val fapManifestApi: FapManifestApi,
    private val queueApi: FapInstallationQueueApi
) : FapInstallationStateManager, LogTagProvider {
    override val TAG = "FapInstallationStateManager"

    override fun getFapStateFlow(
        applicationUid: String,
        currentVersion: SemVer
    ) = combine(
        fapManifestApi.getManifestFlow(),
        queueApi.getFlowById(applicationUid)
    ) { manifests, queueState ->
        return@combine getState(manifests, applicationUid, queueState, currentVersion)
    }

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

        if (manifests == null) {
            return FapState.RetrievingManifest
        }

        val itemFromManifest = manifests.find { it.uid == applicationUid }
            ?: return FapState.ReadyToInstall
        return if (currentVersion > itemFromManifest.version.semVer) {
            FapState.ReadyToUpdate(itemFromManifest)
        } else {
            FapState.Installed
        }
    }

    private fun queueStateToFapState(queueState: FapQueueState) = when (queueState) {
        is FapQueueState.InProgress -> when (queueState.request) {
            is FapActionRequest.Cancel -> FapState.Canceling
            is FapActionRequest.Install -> FapState.InstallationInProgress(queueState.float)
            is FapActionRequest.Update -> FapState.UpdatingInProgress(queueState.float)
            is FapActionRequest.Delete -> FapState.Deleting
        }

        is FapQueueState.Pending -> when (queueState.request) {
            is FapActionRequest.Cancel -> FapState.Canceling
            is FapActionRequest.Install -> FapState.InstallationInProgress(0f)
            is FapActionRequest.Update -> FapState.UpdatingInProgress(0f)
            is FapActionRequest.Delete -> FapState.Deleting
        }

        FapQueueState.NotFound,
        is FapQueueState.Failed -> null
    }
}
