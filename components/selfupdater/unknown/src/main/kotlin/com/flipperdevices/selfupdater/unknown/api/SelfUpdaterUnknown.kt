package com.flipperdevices.selfupdater.unknown.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterUnknown @Inject constructor() : SelfUpdaterApi {
    override suspend fun startCheckUpdate(onEndCheck: suspend (SelfUpdateResult) -> Unit) = Unit

    override fun getInstallSourceName() = "Unknown"
    override fun isSelfUpdateCanManualCheck(): Boolean = false
}
