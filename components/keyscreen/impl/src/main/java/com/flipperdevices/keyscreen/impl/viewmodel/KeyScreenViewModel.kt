package com.flipperdevices.keyscreen.impl.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.keyscreen.api.KeyStateHelperApi
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.StateFlow

@Suppress("LongParameterList")
class KeyScreenViewModel @AssistedInject constructor(
    @Assisted val paramKeyPath: FlipperKeyPath, // For get value to bottom sheet
    keyStateHelperApi: KeyStateHelperApi.Builder,
    private val metricApi: MetricApi
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "KeyScreenViewModel"

    private val keyStateHelper = keyStateHelperApi.build(paramKeyPath, viewModelScope)

    fun getKeyPath(): FlipperKeyPath {
        return (keyStateHelper.getKeyScreenState().value as? KeyScreenState.Ready)
            ?.flipperKey
            ?.getKeyPath()
            ?: paramKeyPath
    }

    fun getKeyScreenState(): StateFlow<KeyScreenState> = keyStateHelper.getKeyScreenState()

    fun setFavorite(isFavorite: Boolean) = keyStateHelper.setFavorite(isFavorite)

    fun onOpenEdit(onEndAction: (FlipperKeyPath) -> Unit) = keyStateHelper.onOpenEdit(onEndAction)

    fun onDelete(onEndAction: () -> Unit) = keyStateHelper.onDelete(onEndAction)

    fun onRestore(onEndAction: () -> Unit) = keyStateHelper.onRestore(onEndAction)

    fun openNfcEditor(onEndAction: (FlipperKeyPath) -> Unit) {
        val state = getKeyScreenState().value
        if (state !is KeyScreenState.Ready) return

        metricApi.reportSimpleEvent(SimpleEvent.OPEN_NFC_DUMP_EDITOR)
        val flipperKey = state.flipperKey
        val flipperKeyPath = flipperKey.getKeyPath()
        onEndAction(flipperKeyPath)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(keyPath: FlipperKeyPath): KeyScreenViewModel
    }
}
