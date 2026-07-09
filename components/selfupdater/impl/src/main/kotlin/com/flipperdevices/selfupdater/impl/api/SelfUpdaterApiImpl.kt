package com.flipperdevices.selfupdater.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.selfupdater.api.SelfUpdaterSourceApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding

@SingleIn(AppGraph::class)
@ContributesBinding(AppGraph::class, binding<SelfUpdaterApi>())
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
        } catch (e: Exception) {
            info { "Self update error: $e" }
            return SelfUpdateResult.ERROR
        } finally {
            inProgressState.value = false
        }
    }

    override fun getInProgressState() = inProgressState.asStateFlow()

    override fun getInstallSourceName(): String = selfUpdaterSourceApi.getInstallSourceName()

    override fun isSelfUpdateCanManualCheck() = selfUpdaterSourceApi.isSelfUpdateCanManualCheck()
}
