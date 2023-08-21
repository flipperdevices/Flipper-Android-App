package com.flipperdevices.selfupdater.unknown.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterUnknown @Inject constructor() : SelfUpdaterApi {
    override fun getState(): StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()

    override fun startCheckUpdate(scope: CoroutineScope, onEndCheck: suspend (SelfUpdateResult) -> Unit) = Unit

    override fun getInstallSourceName() = "Unknown"
    override fun isSelfUpdateCanManualCheck(): Boolean = false
}
