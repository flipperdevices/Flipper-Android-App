package com.flipperdevices.faphub.installation.all.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.installation.all.api.FapInstallationAllApi
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

private const val CHUNK_RECEIVED_COUNT = 50

@ContributesBinding(AppGraph::class, FapInstallationAllApi::class)
class FapInstallationAllImpl @Inject constructor(
    private val fapNetworkApi: FapNetworkApi,
    private val queueApi: FapInstallationQueueApi,
    private val manifestApi: FapManifestApi,
    private val targetProviderApi: FlipperTargetProviderApi
) : FapInstallationAllApi, LogTagProvider {
    override val TAG = "FapInstallationAllApi"

    override suspend fun installAll() {
        info { "Start receive installed apps" }
        val alreadyInstalledApps = manifestApi.getManifestFlow()
            .filter { it is FapManifestState.Loaded && !it.inProgress }
            .mapNotNull { state ->
                (state as? FapManifestState.Loaded)?.items
            }.first()
        info { "Received installed ${alreadyInstalledApps.size} apps" }
        val target = targetProviderApi.getFlipperTarget().filterNotNull().first()
        info { "Received target $target" }
        val allApps = mutableListOf<FapItemShort>()
        var currentOffset = 0
        var receivedAppsCount: Int
        do {
            val receivedApps = fapNetworkApi.getAllItem(
                target = target,
                sortType = SortType.UPDATE_AT_DESC,
                offset = currentOffset,
                limit = CHUNK_RECEIVED_COUNT
            ).getOrThrow()
            info { "Receive ${receivedApps.size} from network, with $currentOffset offset" }
            receivedAppsCount = receivedApps.size
            currentOffset += CHUNK_RECEIVED_COUNT
            allApps.addAll(receivedApps)
        } while (receivedAppsCount == CHUNK_RECEIVED_COUNT)
        info { "Received total ${allApps.size} apps from network" }
        val installedUids = alreadyInstalledApps.map { it.uid }.toSet()
        val appsToInstall = allApps.filterNot { installedUids.contains(it.id) }
        info { "Apps to install: ${appsToInstall.size} apps" }
        appsToInstall.forEach { appItem ->
            queueApi.enqueue(
                FapActionRequest.Install(
                    applicationAlias = appItem.applicationAlias,
                    applicationUid = appItem.id,
                    applicationName = appItem.name,
                    toVersion = appItem.upToDateVersion,
                    categoryAlias = appItem.category.name,
                    iconUrl = appItem.picUrl
                )
            )
        }
        info { "Enqueue done!" }
    }
}
