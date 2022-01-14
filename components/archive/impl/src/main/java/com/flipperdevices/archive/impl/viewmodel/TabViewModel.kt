package com.flipperdevices.archive.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.impl.di.ArchiveComponent
import com.flipperdevices.archive.impl.model.ArchiveTab
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TabViewModel(
    private val tab: ArchiveTab.Specified
) : ViewModel() {
    @Inject
    lateinit var keyApi: KeyApi

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    private val tabKeys = MutableStateFlow<List<FlipperKey>?>(null)

    init {
        ComponentHolder.component<ArchiveComponent>().inject(this)
        viewModelScope.launch {
            keyApi.getKeysAsFlow(tab.fileType).combine(
                synchronizationApi.getSynchronizationState()
            ) { keyList, synchronizationState ->
                if (keyList.isEmpty() && synchronizationState == SynchronizationState.IN_PROGRESS) {
                    return@combine null
                } else {
                    return@combine keyList
                }
            }.onEach {
                tabKeys.emit(it)
            }.launchIn(viewModelScope)
        }
    }

    fun getKeys(): StateFlow<List<FlipperKey>?> = tabKeys

    fun refresh() {
        synchronizationApi.startSynchronization()
    }
}
