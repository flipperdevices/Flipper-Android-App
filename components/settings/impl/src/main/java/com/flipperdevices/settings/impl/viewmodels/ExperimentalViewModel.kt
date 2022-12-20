package com.flipperdevices.settings.impl.viewmodels

import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.filemanager.api.navigation.FileManagerEntry
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject
import javax.inject.Provider

class ExperimentalViewModel @VMInject constructor(
    fileManagerEntryProvider: Provider<FileManagerEntry>,
    metricApiProvider: Provider<MetricApi>,
    private val screenStreamingFeatureEntry: ScreenStreamingFeatureEntry,
    private val dataStore: DataStore<Settings>
) : LifecycleViewModel() {
    private val fileManagerEntry by fileManagerEntryProvider
    private val metricApi by metricApiProvider

    fun onOpenFileManager(navController: NavController) {
        metricApi.reportSimpleEvent(SimpleEvent.EXPERIMENTAL_OPEN_FM)
        navController.navigate(fileManagerEntry.fileManagerDestination())
    }

    fun onOpenScreenStreaming(navController: NavController) {
        metricApi.reportSimpleEvent(SimpleEvent.EXPERIMENTAL_OPEN_SCREENSTREAMING)
        navController.navigate(screenStreamingFeatureEntry.ROUTE.name)
    }

    fun onSwitchApplicationCatalog(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.updateData {
                it.toBuilder()
                    .setApplicationCatalog(enabled)
                    .build()
            }
        }
    }

    fun onSwitchAppsSwitch(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.updateData {
                it.toBuilder()
                    .setFaphubNewSwitch(enabled)
                    .build()
            }
        }
    }
}
