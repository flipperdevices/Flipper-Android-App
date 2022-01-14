package com.flipperdevices.archive.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.impl.di.ArchiveComponent
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.di.ComponentHolder
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class GeneralTabViewModel : ViewModel() {
    @Inject
    lateinit var keyApi: KeyApi

    @Inject
    lateinit var favoriteApi: FavoriteApi

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    private val keys = MutableStateFlow<List<FlipperKey>?>(null)
    private val favoriteKeys = MutableStateFlow<List<FlipperKey>>(emptyList())

    init {
        ComponentHolder.component<ArchiveComponent>().inject(this)
        viewModelScope.launch {
            keyApi.getKeysAsFlow(null).combine(
                synchronizationApi.getSynchronizationState()
            ) { keyList, synchronizationState ->
                if (keyList.isEmpty() && synchronizationState == SynchronizationState.IN_PROGRESS) {
                    return@combine null
                } else {
                    return@combine keyList
                }
            }.combine(favoriteApi.getFavoritesFlow()) { keyList, favoriteKeysList ->
                keys.emit(keyList?.minus(favoriteKeysList.toSet()))
                favoriteKeys.emit(favoriteKeysList)
            }.launchIn(viewModelScope)
        }
    }

    fun getKeys(): StateFlow<List<FlipperKey>?> = keys
    fun getFavoriteKeys(): StateFlow<List<FlipperKey>> = favoriteKeys

    fun refresh() {
        synchronizationApi.startSynchronization()
    }
}
