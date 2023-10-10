package com.flipperdevices.settings.impl.viewmodels

import androidx.navigation.NavController
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.filemanager.api.navigation.FileManagerEntry
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import tangle.viewmodel.VMInject
import javax.inject.Provider

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
}
