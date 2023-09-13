package com.flipperdevices.selfupdater.unknown.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.selfupdater.api.SelfUpdaterSourceApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SelfUpdaterSourceApi::class)
class SelfUpdaterUnknown @Inject constructor() : SelfUpdaterSourceApi {
    override suspend fun checkUpdate(manual: Boolean): SelfUpdateResult {
        throw NotImplementedError()
    }

    override fun getInstallSourceName() = "Unknown"
    override fun isSelfUpdateCanManualCheck(): Boolean = false
}
