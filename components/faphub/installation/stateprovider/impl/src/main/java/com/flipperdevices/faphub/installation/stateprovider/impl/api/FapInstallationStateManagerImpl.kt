package com.flipperdevices.faphub.installation.stateprovider.impl.api

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.flipperdevices.faphub.target.model.FlipperTarget
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapInstallationStateManager::class)
class FapInstallationStateManagerImpl @Inject constructor(
    private val fapManifestApi: FapManifestApi,
    private val queueApi: FapInstallationQueueApi,
    private val flipperTargetProviderApi: FlipperTargetProviderApi
) : FapInstallationStateManager, LogTagProvider {
    override val TAG = "FapInstallationStateManager"

    override fun getFapStateFlow(
        applicationUid: String,
        currentVersion: SemVer
    ) = combine(
        fapManifestApi.getManifestFlow(),
        queueApi.getFlowById(applicationUid),
        flipperTargetProviderApi.getFlipperTarget()
    ) { manifests, queueState, target ->
        return@combine getState(manifests, applicationUid, queueState, currentVersion, target)
    }

    @Suppress("UnusedPrivateMember")
    private suspend fun getState(
        manifest: FapManifestState,
        applicationUid: String,
        queueState: FapQueueState,
        currentVersion: SemVer,
        target: FlipperTarget?
    ): FapState {
        val stateFromQueue = queueStateToFapState(queueState)
        if (stateFromQueue != null) {
            return stateFromQueue
        }

        if (target == null) {
            return FapState.RetrievingManifest
        }

        when (target) {
            FlipperTarget.NotConnected -> return FapState.ConnectFlipper
            FlipperTarget.Unsupported -> return FapState.FlipperOutdated
            is FlipperTarget.Received -> {}
        }

        val stateFromManifest = manifestStateToFapState(
            manifest,
            applicationUid,
            currentVersion,
            target
        )

        if (stateFromManifest != null) {
            return stateFromManifest
        }

        return FapState.ReadyToInstall
    }

    private fun manifestStateToFapState(
        manifest: FapManifestState,
        applicationUid: String,
        currentVersion: SemVer,
        flipperTarget: FlipperTarget.Received
    ) = when (manifest) {
        is FapManifestState.Loaded -> manifest.items.find { it.fapManifestItem.uid == applicationUid }
            ?.let { fapManifestEnrichedItem ->
                val sdkApi = fapManifestEnrichedItem.fapManifestItem.sdkApi
                if (fapManifestEnrichedItem.numberVersion > currentVersion) {
                    return@let FapState.ReadyToUpdate(fapManifestEnrichedItem.fapManifestItem)
                } else if (sdkApi == null || sdkApi > flipperTarget.sdk) {
                    return@let FapState.ReadyToUpdate(fapManifestEnrichedItem.fapManifestItem)
                } else {
                    return@let FapState.Installed
                }
            }

        is FapManifestState.LoadedOffline -> if (manifest.items.find { it.uid == applicationUid } != null) {
            FapState.Installed
        } else {
            null
        }

        FapManifestState.Loading,
        FapManifestState.NotLoaded -> FapState.RetrievingManifest
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
