package com.flipperdevices.widget.impl.storage

import com.flipperdevices.bridge.dao.api.delegates.WidgetDataApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.keyscreen.api.EmulateHelper
import com.flipperdevices.widget.impl.model.WidgetState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex

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
        stateMap[widgetId] = state
    }

    override suspend fun getState(
        widgetId: Int
    ): WidgetState = withLockResult(mutex, "get") {
        val widgetData = widgetDataApi.getWidgetDataByWidgetId(widgetId)
        val widgetKeyPath = widgetData?.flipperKeyPath
        if (widgetKeyPath != null) {
            if (emulateHelper.getCurrentEmulatingKey().value == widgetKeyPath.path) {
                return@withLockResult WidgetState.IN_PROGRESS
            }
        }

        return@withLockResult stateMap[widgetId] ?: WidgetState.PENDING
    }
}