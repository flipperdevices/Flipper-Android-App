package com.flipperdevices.selfupdater.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.selfupdater.api.SelfUpdaterSourceApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterApiImpl @Inject constructor(
    private val selfUpdaterSourceApi: SelfUpdaterSourceApi
) : SelfUpdaterApi, LogTagProvider {
    override val TAG: String = "SelfUpdaterApi"

    private val inProgressState = MutableStateFlow(false)

    override suspend fun startCheckUpdate(manual: Boolean): SelfUpdateResult {
        info { "Start check update" }
        if (!inProgressState.compareAndSet(expect = false, update = true)) {
            info { "Self update in progress" }
            return SelfUpdateResult.IN_PROGRESS
        }
        return try {
            selfUpdaterSourceApi.checkUpdate(manual = manual)
        } finally {
            inProgressState.emit(false)
        }
    }

    override fun getInProgressState() = inProgressState.asStateFlow()

    override fun getInstallSourceName(): String = selfUpdaterSourceApi.getInstallSourceName()

    override fun isSelfUpdateCanManualCheck() = selfUpdaterSourceApi.isSelfUpdateCanManualCheck()
}
