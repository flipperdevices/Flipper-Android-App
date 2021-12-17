package com.flipperdevices.archive.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.impl.di.ArchiveComponent
import com.flipperdevices.archive.impl.model.ArchiveTab
import com.flipperdevices.bridge.dao.api.DaoApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.di.ComponentHolder
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TabViewModel(
    private val tab: ArchiveTab
) : ViewModel() {
    @Inject
    lateinit var daoApi: DaoApi

    private val tabKeys = MutableStateFlow<List<FlipperKey>>(emptyList())

    init {
        ComponentHolder.component<ArchiveComponent>().inject(this)
        viewModelScope.launch {
            daoApi.getKeysApi().getKeysAsFlow(tab.fileType).collect {
                tabKeys.emit(it)
            }
        }
    }

    fun getKeys(): StateFlow<List<FlipperKey>> = tabKeys
}
