package com.flipperdevices.keyscreen.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.keyscreen.model.KeyScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface KeyStateHelperApi {
    fun getKeyScreenState(): StateFlow<KeyScreenState>
    fun setFavorite(isFavorite: Boolean)
    fun onDelete(onEndAction: () -> Unit)
    fun onRestore(onEndAction: () -> Unit)
    fun onOpenEdit(onEndAction: (FlipperKeyPath) -> Unit)

    interface Builder {
        fun build(
            flipperKeyPath: FlipperKeyPath,
            scope: CoroutineScope
        ): KeyStateHelperApi
    }
}
