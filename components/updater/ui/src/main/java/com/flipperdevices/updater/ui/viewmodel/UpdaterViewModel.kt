package com.flipperdevices.updater.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.ui.di.UpdaterComponent
import javax.inject.Inject

class UpdaterViewModel : ViewModel() {
    @Inject
    lateinit var updaterApi: UpdaterApi

    init {
        ComponentHolder.component<UpdaterComponent>().inject(this)
    }

    fun onStart(updaterDist: DistributionFile) {
        updaterApi.start(updaterDist)
    }
}
