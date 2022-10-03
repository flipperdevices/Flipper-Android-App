package com.flipperdevices.archive.api

import androidx.compose.foundation.lazy.LazyListScope
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.github.terrakok.cicerone.Screen

interface ArchiveApi {
    fun getArchiveScreen(): Screen

    fun LazyListScope.ComposableKeysGridWithSynchronization(
        keys: List<FlipperKey>,
        synchronizationUiApi: SynchronizationUiApi,
        synchronizationState: SynchronizationState
    )
}
