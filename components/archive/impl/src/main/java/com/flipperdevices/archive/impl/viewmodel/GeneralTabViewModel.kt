package com.flipperdevices.archive.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.api.SearchApi
import com.flipperdevices.archive.impl.di.ArchiveComponent
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.github.terrakok.cicerone.ResultListener
import com.github.terrakok.cicerone.ResultListenerHandler
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
    private val searchApi: SearchApi,
    private val keyScreenApi: KeyScreenApi,
    private val ciceroneGlobal: CiceroneGlobal
) : ViewModel(), ResultListener {
    private val keys = MutableStateFlow<List<FlipperKey>>(emptyList())
    private val favoriteKeys = MutableStateFlow<List<FlipperKey>>(emptyList())
    private val synchronizationState =
        MutableStateFlow<SynchronizationState>(SynchronizationState.NotStarted)
    private var resultListenerDispatcher: ResultListenerHandler? = null

    init {
        ComponentHolder.component<ArchiveComponent>().inject(this)
        viewModelScope.launch {
            simpleKeyApi.getExistKeysAsFlow(null)
                .combine(favoriteApi.getFavoritesFlow()) { keyList, favoriteKeysList ->
                    val favoriteKeyPaths = favoriteKeysList.map { it.path }.toSet()
                    val keysExceptFavorite =
                        keyList.filterNot { favoriteKeyPaths.contains(it.path) }
                    keys.emit(keysExceptFavorite)
                    favoriteKeys.emit(favoriteKeysList)
                }.launchIn(viewModelScope)
            synchronizationApi.getSynchronizationState().onEach {
                synchronizationState.emit(it)
            }.launchIn(viewModelScope)
        }
    }

    fun getKeys(): StateFlow<List<FlipperKey>?> = keys
    fun getFavoriteKeys(): StateFlow<List<FlipperKey>> = favoriteKeys
    fun getSynchronizationState(): StateFlow<SynchronizationState> = synchronizationState

    fun onOpenSearch() {
        val router = ciceroneGlobal.getRouter()
        router.navigateTo(searchApi.getSearchScreen())
        resultListenerDispatcher?.dispose()
        resultListenerDispatcher =
            router.setResultListener(SearchApi.SEARCH_RESULT_KEY, this)
    }

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
