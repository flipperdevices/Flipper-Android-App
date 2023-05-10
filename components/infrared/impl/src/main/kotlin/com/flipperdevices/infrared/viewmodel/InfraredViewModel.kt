package com.flipperdevices.infrared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.infrared.InfraredRemote
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.infrared.api.EXTRA_KEY_PATH
import com.flipperdevices.keyscreen.api.state.DeleteState
import com.flipperdevices.keyscreen.api.state.FavoriteState
import com.flipperdevices.keyscreen.api.state.KeyScreenState
import com.flipperdevices.keyscreen.api.state.KeyStateHelperApi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class InfraredViewModel @VMInject constructor(
    @TangleParam(EXTRA_KEY_PATH)
    private val keyPath: FlipperKeyPath,
    keyStateHelperApi: KeyStateHelperApi.Builder,
) : ViewModel(), LogTagProvider {
    override val TAG = "InfraredViewModel"

    private val keyStateHelper = keyStateHelperApi.build(keyPath, viewModelScope)
    fun keyState() = keyStateHelper.getKeyScreenState()

    private val controlState = MutableStateFlow<ImmutableList<InfraredRemote>>(persistentListOf())
    fun controlState() = controlState.asStateFlow()

    init {
        viewModelScope.launch {
            keyStateHelper.getKeyScreenState().collectLatest(::collectControls)
        }
    }

    private suspend fun collectControls(state: KeyScreenState) {
        if (state !is KeyScreenState.Ready) {
            controlState.emit(persistentListOf())
            return
        }

        val keyParser = state.parsedKey
        if (keyParser !is FlipperKeyParsed.Infrared) {
            controlState.emit(persistentListOf())
            return
        }

        controlState.emit(keyParser.remotes.toImmutableList())
    }

    fun onRename(action: (FlipperKeyPath) -> Unit) = keyStateHelper.onOpenEdit(action)

    fun onDelete(action: () -> Unit) {
        val state = keyStateHelper.getKeyScreenState().value
        if (state !is KeyScreenState.Ready) return

        when (state.deleteState) {
            DeleteState.DELETED -> return
            DeleteState.PROGRESS -> return
            DeleteState.NOT_DELETED -> keyStateHelper.onDelete(action)
        }
    }

    fun onFavorite() {
        val state = keyStateHelper.getKeyScreenState().value
        if (state !is KeyScreenState.Ready) return

        keyStateHelper.setFavorite(state.favoriteState != FavoriteState.FAVORITE)
    }
}
