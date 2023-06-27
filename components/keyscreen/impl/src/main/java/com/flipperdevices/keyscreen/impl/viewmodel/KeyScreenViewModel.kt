package com.flipperdevices.keyscreen.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyscreen.api.KeyStateHelperApi
import com.flipperdevices.keyscreen.impl.api.EXTRA_KEY_PATH
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import kotlinx.coroutines.flow.StateFlow
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

@Suppress("LongParameterList")
class KeyScreenViewModel @VMInject constructor(
    @TangleParam(EXTRA_KEY_PATH)
    keyPath: FlipperKeyPath,
    keyStateHelperApi: KeyStateHelperApi.Builder,
    private val metricApi: MetricApi
) : ViewModel(), LogTagProvider {
    override val TAG = "KeyScreenViewModel"

    private val keyStateHelper = keyStateHelperApi.build(keyPath, viewModelScope)

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

    fun getEmulateConfig(): EmulateConfig? {
        val state = getKeyScreenState().value
        if (state !is KeyScreenState.Ready) return null

        val flipperKey = state.flipperKey
        val keyType = flipperKey.flipperKeyType
        val parsedKey = state.parsedKey

        if (keyType == null || keyType == FlipperKeyType.INFRARED) {
            return null
        }

        val timeout = if (parsedKey is FlipperKeyParsed.SubGhz &&
            parsedKey.totalTimeMs != null
        ) {
            parsedKey.totalTimeMs
        } else {
            null
        }

        return EmulateConfig(
            keyType = keyType,
            keyPath = flipperKey.path,
            minEmulateTime = timeout,
        )
    }
}
