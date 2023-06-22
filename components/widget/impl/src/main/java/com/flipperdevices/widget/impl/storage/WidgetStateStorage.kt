package com.flipperdevices.widget.impl.storage

import com.flipperdevices.bridge.dao.api.delegates.WidgetDataApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.widget.impl.model.WidgetState
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

interface WidgetStateStorage {
    suspend fun updateState(widgetId: Int, state: WidgetState)
    suspend fun getState(widgetId: Int): WidgetState
}

@Singleton
@ContributesBinding(AppGraph::class, WidgetStateStorage::class)
class WidgetStateStorageImpl @Inject constructor(
    private val emulateHelper: EmulateHelper,
    private val widgetDataApi: WidgetDataApi
) : WidgetStateStorage, LogTagProvider {
    override val TAG = "WidgetStateStorage"

    private val stateMap = HashMap<Int, WidgetState>()
    private val mutex = Mutex()

    override suspend fun updateState(
        widgetId: Int,
        state: WidgetState
    ) = withLock(mutex, "update") {
        info { "Update state for $widgetId to $state" }
        if (state == WidgetState.IN_PROGRESS) {
            stateMap.forEach {
                if (it.value == WidgetState.IN_PROGRESS) {
                    stateMap[it.key] = WidgetState.PENDING
                }
            }
        }
        stateMap[widgetId] = state
    }

    override suspend fun getState(
        widgetId: Int
    ): WidgetState = withLockResult(mutex, "get") {
        val state = stateMap[widgetId]
        info { "Get widget state for $widgetId, state: $state" }
        val widgetData = widgetDataApi.getWidgetDataByWidgetId(widgetId)
        val widgetKeyPath = widgetData?.flipperKeyPath
        if (widgetKeyPath != null) {
            info { "Widget key path for $widgetId is $widgetKeyPath" }
            val currentActiveEmulating = emulateHelper.getCurrentEmulatingKey().value
            if (currentActiveEmulating?.keyPath == widgetKeyPath.path) {
                info {
                    "Current active emulating is $currentActiveEmulating, so return IN_PROGRESS"
                }
                return@withLockResult WidgetState.IN_PROGRESS
            }
        }

        return@withLockResult state ?: WidgetState.PENDING
    }
}
