package com.flipperdevices.updater.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.ui.di.UpdaterComponent
import javax.inject.Inject
import kotlinx.coroutines.launch

class UpdaterViewModel : ViewModel() {
    @Inject
    lateinit var updaterApi: UpdaterApi

    init {
        ComponentHolder.component<UpdaterComponent>().inject(this)
    }

    fun getState() = updaterApi.getState()

    fun onStart(updaterDist: DistributionFile) {
        updaterApi.start(updaterDist)
    }

    fun onCancel() = viewModelScope.launch {
        updaterApi.cancel()
    }
}
