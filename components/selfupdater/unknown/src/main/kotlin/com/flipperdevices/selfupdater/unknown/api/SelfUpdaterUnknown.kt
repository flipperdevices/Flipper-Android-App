package com.flipperdevices.selfupdater.unknown.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.selfupdater.api.SelfUpdaterSourceApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<SelfUpdaterSourceApi>())
class SelfUpdaterUnknown @Inject constructor() : SelfUpdaterSourceApi {
    override suspend fun checkUpdate(manual: Boolean): SelfUpdateResult {
        return SelfUpdateResult.NO_UPDATES
    }

    override fun getInstallSourceName() = "Unknown"
    override fun isSelfUpdateCanManualCheck(): Boolean = false
}
