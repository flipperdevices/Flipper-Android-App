package com.flipperdevices.settings.impl.viewmodels

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.filemanager.api.navigation.FileManagerScreenProvider
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.settings.impl.di.SettingsComponent
import com.github.terrakok.cicerone.Router
import javax.inject.Inject

class ExperimentalViewModel : LifecycleViewModel() {
    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var fileManager: FileManagerScreenProvider

    @Inject
    lateinit var dataStoreSetting: DataStore<Settings>

    @Inject
    lateinit var metricApi: MetricApi

    init {
        ComponentHolder.component<SettingsComponent>().inject(this)
    }

    fun onOpenFileManager(router: Router) {
        metricApi.reportSimpleEvent(SimpleEvent.EXPERIMENTAL_OPEN_FM)
        router.navigateTo(fileManager.fileManager())
    }
}
