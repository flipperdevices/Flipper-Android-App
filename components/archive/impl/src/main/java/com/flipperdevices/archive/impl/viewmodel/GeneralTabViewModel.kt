package com.flipperdevices.archive.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.api.SearchApi
import com.flipperdevices.archive.impl.di.ArchiveComponent
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.di.ComponentHolder
import com.github.terrakok.cicerone.Router
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GeneralTabViewModel : ViewModel() {
    @Inject
    lateinit var keyApi: KeyApi

    @Inject
    lateinit var favoriteApi: FavoriteApi

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    @Inject
    lateinit var searchApi: SearchApi

    private val keys = MutableStateFlow<List<FlipperKey>>(emptyList())
    private val favoriteKeys = MutableStateFlow<List<FlipperKey>>(emptyList())
    private val synchronizationState =
        MutableStateFlow(SynchronizationState.NOT_STARTED)

    init {
        ComponentHolder.component<ArchiveComponent>().inject(this)
        viewModelScope.launch {
            keyApi.getExistKeysAsFlow(null)
                .combine(favoriteApi.getFavoritesFlow()) { keyList, favoriteKeysList ->
                    // val favoriteKeyPaths = favoriteKeysList.map { it.path }.toSet()
                    // val keysExceptFavorite = keyList?.filterNot { favoriteKeyPaths.contains(it.path) }
                    keys.emit(keyList)
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

    fun onOpenSearch(router: Router) {
        router.navigateTo(searchApi.getSearchScreen())
    }

    fun refresh() {
        synchronizationApi.startSynchronization()
    }
}
