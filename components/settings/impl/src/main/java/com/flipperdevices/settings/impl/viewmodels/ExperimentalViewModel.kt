package com.flipperdevices.settings.impl.viewmodels

import androidx.navigation.NavController
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.filemanager.api.navigation.FileManagerEntry
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.settings.impl.model.NavGraphRoute
import javax.inject.Provider
import tangle.viewmodel.VMInject

class ExperimentalViewModel @VMInject constructor(
    fileManagerEntryProvider: Provider<FileManagerEntry>,
    metricApiProvider: Provider<MetricApi>
) : LifecycleViewModel() {
    private val fileManagerEntry by fileManagerEntryProvider
    private val metricApi by metricApiProvider

    fun onOpenFileManager(navController: NavController) {
        metricApi.reportSimpleEvent(SimpleEvent.EXPERIMENTAL_OPEN_FM)
        navController.navigate(fileManagerEntry.fileManagerDestination())
    }

    fun onOpenScreenStreaming(navController: NavController) {
        metricApi.reportSimpleEvent(SimpleEvent.EXPERIMENTAL_OPEN_SCREENSTREAMING)
        navController.navigate(NavGraphRoute.ScreenStreaming.name)
    }
}
