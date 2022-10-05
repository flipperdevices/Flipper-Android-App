package com.flipperdevices.widget.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.api.SearchApi
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.github.terrakok.cicerone.ResultListener
import com.github.terrakok.cicerone.ResultListenerHandler
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class WidgetSelectViewModel @VMInject constructor(
    private val simpleKeyApi: SimpleKeyApi,
    private val favoriteApi: FavoriteApi,
    private val searchApi: SearchApi,
    private val synchronizationApi: SynchronizationApi
) : ViewModel(), ResultListener {
    private val keys = MutableStateFlow<List<FlipperKey>>(emptyList())
    private val favoriteKeys = MutableStateFlow<List<FlipperKey>>(emptyList())
    private val synchronizationState =
        MutableStateFlow<SynchronizationState>(SynchronizationState.NotStarted)
    private var resultListenerDispatcher: ResultListenerHandler? = null

    init {
        viewModelScope.launch(Dispatchers.Default) {
            simpleKeyApi.getExistKeysAsFlow(null)
                .combine(favoriteApi.getFavoritesFlow()) { keyList, favoriteKeysList ->
                    val favoriteKeyPaths = favoriteKeysList.map { it.path }.toSet()
                    val keysExceptFavorite =
                        keyList.filterNot { favoriteKeyPaths.contains(it.path) }
                    keys.emit(keysExceptFavorite)
                    favoriteKeys.emit(favoriteKeysList)
                }.collect()
        }
        synchronizationApi.getSynchronizationState().onEach {
            synchronizationState.emit(it)
        }.launchIn(viewModelScope)
    }

    fun getKeysFlow(): StateFlow<List<FlipperKey>> = keys
    fun getFavoriteKeysFlow(): StateFlow<List<FlipperKey>> = favoriteKeys
    fun getSynchronizationFlow(): StateFlow<SynchronizationState> = synchronizationState


    fun refresh() {
        synchronizationApi.startSynchronization(force = true)
    }

    fun onOpenSearch(router: Router) {
        router.navigateTo(searchApi.getSearchScreen(exitOnOpen = true))
    }

    override fun onResult(data: Any) {
        if (data is FlipperKeyPath) {
            onSelectKey(data)
        }
    }

    fun onSelectKey(keyPath: FlipperKeyPath) = Unit

    override fun onCleared() {
        super.onCleared()
        resultListenerDispatcher?.dispose()
    }
}