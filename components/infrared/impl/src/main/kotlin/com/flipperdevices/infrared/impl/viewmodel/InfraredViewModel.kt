package com.flipperdevices.infrared.impl.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.infrared.api.InfraredConnectionApi
import com.flipperdevices.keyscreen.api.KeyStateHelperApi
import com.flipperdevices.keyscreen.model.KeyScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class InfraredViewModel @AssistedInject constructor(
    @Assisted private val paramKeyPath: FlipperKeyPath, // For get value to bottom sheet
    keyStateHelperApi: KeyStateHelperApi.Builder,
    private val infraredConnectionApi: InfraredConnectionApi
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "InfraredViewModel"

    private val keyStateHelper = keyStateHelperApi.build(paramKeyPath, viewModelScope)
    fun getState() = keyStateHelper.getKeyScreenState()

    fun getKeyPath(): FlipperKeyPath {
        return (keyStateHelper.getKeyScreenState().value as? KeyScreenState.Ready)
            ?.flipperKey
            ?.getKeyPath()
            ?: paramKeyPath
    }

    private val emulateStateFlow = infraredConnectionApi.getState()
        .onEach { info { "#onServiceApiReady $it" } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun getEmulateState() = emulateStateFlow

    fun setFavorite(isFavorite: Boolean) = keyStateHelper.setFavorite(isFavorite)

    fun onRename(onEndAction: (FlipperKeyPath) -> Unit) = keyStateHelper.onOpenEdit(onEndAction)

    fun onDelete(onEndAction: () -> Unit) = keyStateHelper.onDelete(onEndAction)

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            keyPath: FlipperKeyPath
        ): InfraredViewModel
    }
}
