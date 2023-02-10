package com.flipperdevices.archive.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.github.terrakok.cicerone.ResultListener
import com.github.terrakok.cicerone.ResultListenerHandler
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class GeneralTabViewModel @VMInject constructor(
    private val simpleKeyApi: SimpleKeyApi,
    private val favoriteApi: FavoriteApi,
    private val synchronizationApi: SynchronizationApi,
    private val keyScreenApi: KeyScreenApi,
    private val ciceroneGlobal: CiceroneGlobal
) : ViewModel(), ResultListener {
    private val keys = MutableStateFlow<ImmutableList<FlipperKey>>(persistentListOf())
    private val favoriteKeys = MutableStateFlow<ImmutableList<FlipperKey>>(persistentListOf())
    private val synchronizationState =
        MutableStateFlow<SynchronizationState>(SynchronizationState.NotStarted)
    private var resultListenerDispatcher: ResultListenerHandler? = null

    init {
        viewModelScope.launch {
            simpleKeyApi.getExistKeysAsFlow(null)
                .combine(favoriteApi.getFavoritesFlow()) { keyList, favoriteKeysList ->
                    val favoriteKeyPaths = favoriteKeysList.map { it.path }.toSet()
                    val keysExceptFavorite =
                        keyList.filterNot { favoriteKeyPaths.contains(it.path) }
                    keys.emit(keysExceptFavorite.toImmutableList())
                    favoriteKeys.emit(favoriteKeysList.toImmutableList())
                }.launchIn(viewModelScope)
            synchronizationApi.getSynchronizationState().onEach {
                synchronizationState.emit(it)
            }.launchIn(viewModelScope)
        }
    }

    fun getKeys(): StateFlow<ImmutableList<FlipperKey>?> = keys
    fun getFavoriteKeys(): StateFlow<ImmutableList<FlipperKey>> = favoriteKeys
    fun getSynchronizationState(): StateFlow<SynchronizationState> = synchronizationState

    fun refresh() {
        synchronizationApi.startSynchronization(force = true)
    }

    fun cancelSynchronization() {
        viewModelScope.launch {
            synchronizationApi.stop()
        }
    }

    override fun onResult(data: Any) {
        if (data is FlipperKeyPath) {
            ciceroneGlobal.getRouter().navigateTo(
                keyScreenApi.getKeyScreenScreen(data)
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        resultListenerDispatcher?.dispose()
    }
}
