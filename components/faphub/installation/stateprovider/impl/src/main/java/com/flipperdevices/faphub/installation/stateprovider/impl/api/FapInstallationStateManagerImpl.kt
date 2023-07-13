package com.flipperdevices.faphub.installation.stateprovider.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.faphub.dao.api.model.FapBuildState
import com.flipperdevices.faphub.dao.api.model.FapItemVersion
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.faphub.installation.stateprovider.api.model.NotAvailableReason
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
        currentVersion: FapItemVersion
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
        currentVersion: FapItemVersion,
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
            FlipperTarget.Unsupported -> return FapState.NotAvailableForInstall(
                NotAvailableReason.FLIPPER_OUTDATED
            )

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

        when (currentVersion.buildState) {
            FapBuildState.READY, FapBuildState.READY_ON_RELEASE -> {
            }

            FapBuildState.BUILD_RUNNING -> return FapState.NotAvailableForInstall(NotAvailableReason.BUILD_RUNNING)

            FapBuildState.UNSUPPORTED_APP -> return FapState.NotAvailableForInstall(
                NotAvailableReason.UNSUPPORTED_APP
            )

            FapBuildState.FLIPPER_OUTDATED -> return FapState.NotAvailableForInstall(
                NotAvailableReason.FLIPPER_OUTDATED
            )

            FapBuildState.UNSUPPORTED_SDK -> return FapState.NotAvailableForInstall(
                NotAvailableReason.UNSUPPORTED_SDK
            )
        }

        return FapState.ReadyToInstall
    }

    private fun manifestStateToFapState(
        manifest: FapManifestState,
        applicationUid: String,
        currentVersion: FapItemVersion,
        flipperTarget: FlipperTarget.Received
    ) = when (manifest) {
        is FapManifestState.Loaded -> manifest.items.find { it.fapManifestItem.uid == applicationUid }
            ?.let { fapManifestEnrichedItem ->
                val sdkApi = fapManifestEnrichedItem.fapManifestItem.sdkApi
                if (currentVersion.buildState != FapBuildState.READY) {
                    return@let FapState.Installed
                } else if (fapManifestEnrichedItem.numberVersion > currentVersion.version) {
                    return@let FapState.ReadyToUpdate(fapManifestEnrichedItem.fapManifestItem)
                } else if (sdkApi == null || sdkApi != flipperTarget.sdk) {
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

        FapManifestState.Loading, is FapManifestState.NotLoaded -> FapState.RetrievingManifest
    }

    private fun queueStateToFapState(queueState: FapQueueState) = when (queueState) {
        is FapQueueState.InProgress -> when (queueState.request) {
            is FapActionRequest.Cancel -> FapState.Canceling
            is FapActionRequest.Install -> FapState.InstallationInProgress(
                active = true,
                progress = queueState.float
            )

            is FapActionRequest.Update -> FapState.UpdatingInProgress(
                active = true,
                progress = queueState.float
            )

            is FapActionRequest.Delete -> FapState.Deleting
        }

        is FapQueueState.Pending -> when (queueState.request) {
            is FapActionRequest.Cancel -> FapState.Canceling
            is FapActionRequest.Install -> FapState.InstallationInProgress(
                active = false,
                progress = 0f
            )

            is FapActionRequest.Update -> FapState.UpdatingInProgress(active = false, progress = 0f)
            is FapActionRequest.Delete -> FapState.Deleting
        }

        FapQueueState.NotFound, is FapQueueState.Failed -> null
    }
}
