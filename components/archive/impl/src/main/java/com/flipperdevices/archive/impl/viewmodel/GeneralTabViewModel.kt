package com.flipperdevices.archive.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

class GeneralTabViewModel @Inject constructor(
    private val simpleKeyApi: SimpleKeyApi,
    private val favoriteApi: FavoriteApi,
    private val synchronizationApi: SynchronizationApi,
) : DecomposeViewModel() {
    private val keys = MutableStateFlow<ImmutableList<FlipperKey>>(persistentListOf())
    private val favoriteKeys = MutableStateFlow<ImmutableList<FlipperKey>>(persistentListOf())
    private val synchronizationState =
        MutableStateFlow<SynchronizationState>(SynchronizationState.NotStarted)

    init {
        viewModelScope.launch(Dispatchers.Default) {
            simpleKeyApi.getExistKeysAsFlow(null)
                .combine(favoriteApi.getFavoritesFlow()) { keyList, favoriteKeysList ->
                    val favoriteKeyPaths = favoriteKeysList.map { it.path }.toSet()
                    val keysExceptFavorite =
                        keyList.filterNot { favoriteKeyPaths.contains(it.path) }
                    keys.emit(keysExceptFavorite.toImmutableList())
                    favoriteKeys.emit(favoriteKeysList.toImmutableList())
                }.launchIn(viewModelScope + Dispatchers.Default)
            synchronizationApi.getSynchronizationState().onEach {
                synchronizationState.emit(it)
            }.launchIn(viewModelScope + Dispatchers.Default)
        }
    }

    fun getKeys(): StateFlow<ImmutableList<FlipperKey>?> = keys
    fun getFavoriteKeys(): StateFlow<ImmutableList<FlipperKey>> = favoriteKeys
    fun getSynchronizationState(): StateFlow<SynchronizationState> = synchronizationState

    fun refresh() {
        synchronizationApi.startSynchronization(force = true)
    }

    fun cancelSynchronization() {
        viewModelScope.launch(Dispatchers.Default) {
            synchronizationApi.stop()
        }
    }
}
