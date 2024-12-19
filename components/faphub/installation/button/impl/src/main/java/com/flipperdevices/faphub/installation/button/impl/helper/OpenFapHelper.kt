package com.flipperdevices.faphub.installation.button.impl.helper

import com.flipperdevices.bridge.connection.feature.appstart.api.FAppStartFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcAppSystemLockedException
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.dao.api.model.FapBuildState
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.impl.model.OpenFapResult
import com.flipperdevices.faphub.installation.button.impl.model.OpenFapState
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import okio.Path.Companion.toPath
import javax.inject.Inject
import javax.inject.Singleton

interface OpenFapHelper {
    fun getOpenFapState(fapButtonConfig: FapButtonConfig?): Flow<OpenFapState>
    suspend fun loadFap(
        config: FapButtonConfig,
        onResult: (OpenFapResult) -> Unit
    )
}

@Singleton
@ContributesBinding(AppGraph::class, OpenFapHelper::class)
class OpenFapHelperImpl @Inject constructor(
    private val fFeatureProvider: FFeatureProvider
) : OpenFapHelper, LogTagProvider {
    override val TAG: String = "OpenFapHelperImpl"

    private val currentOpenAppFlow = MutableStateFlow<FapButtonConfig?>(null)

    override fun getOpenFapState(fapButtonConfig: FapButtonConfig?): Flow<OpenFapState> {
        return combine(
            flow = fFeatureProvider.get<FAppStartFeatureApi>(),
            flow2 = currentOpenAppFlow,
            transform = { status, currentApp ->
                when {
                    status is FFeatureStatus.Unsupported -> OpenFapState.NotSupported
                    status is FFeatureStatus.NotFound -> OpenFapState.NotSupported
                    fapButtonConfig == null -> OpenFapState.NotSupported

                    fapButtonConfig.version.buildState != FapBuildState.READY -> {
                        OpenFapState.NotSupported
                    }

                    currentApp != null -> OpenFapState.InProgress(currentApp)

                    else -> OpenFapState.Ready
                }
            }
        )
    }

    override suspend fun loadFap(
        config: FapButtonConfig,
        onResult: (OpenFapResult) -> Unit
    ) {
        if (currentOpenAppFlow.compareAndSet(null, config).not()) {
            info { "Cannot open because state not ready" }
            return
        }

        val path = APPS_PATH
            .resolve(config.categoryAlias)
            .resolve(config.applicationAlias.plus(".fap"))
        val fAppStartFeatureApi = fFeatureProvider.getSync<FAppStartFeatureApi>()
        if (fAppStartFeatureApi == null) {
            currentOpenAppFlow.emit(null)
            onResult(OpenFapResult.Error)
            return
        }

        currentOpenAppFlow.emit(null)
        fAppStartFeatureApi.startApp(path)
            .onFailure {
                error(it) { "#loadFap could not open app" }
                val result = when (it) {
                    is FRpcAppSystemLockedException -> OpenFapResult.FlipperIsBusy
                    else -> OpenFapResult.Error
                }
                onResult(result)
            }
            .onSuccess {
                onResult(OpenFapResult.AllGood)
            }
    }

    companion object {
        // TODO move somewhere else
        private val APPS_PATH = "/ext/apps/".toPath()
    }
}
