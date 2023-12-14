package com.flipperdevices.settings.impl.viewmodels

import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.settings.impl.model.SettingsNavigationConfig
import javax.inject.Inject
import javax.inject.Provider

class ExperimentalViewModel @Inject constructor(
    metricApiProvider: Provider<MetricApi>
) : LifecycleViewModel() {
    private val metricApi by metricApiProvider

    fun onOpenFileManager(navigation: StackNavigation<SettingsNavigationConfig>) {
        metricApi.reportSimpleEvent(SimpleEvent.EXPERIMENTAL_OPEN_FM)
        navigation.push(SettingsNavigationConfig.FileManager)
    }
}
