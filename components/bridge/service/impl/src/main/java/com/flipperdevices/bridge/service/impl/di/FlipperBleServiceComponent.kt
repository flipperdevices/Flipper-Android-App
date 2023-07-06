package com.flipperdevices.bridge.service.impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener
import com.flipperdevices.bridge.service.impl.FlipperServiceApiImpl
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Provider
import kotlinx.coroutines.CoroutineScope

@ContributesTo(AppGraph::class)
interface FlipperBleServiceComponentDependencies {
    val pairSettingsStore: DataStore<PairSettings>
    val settingsStore: DataStore<Settings>
    val metricApi: MetricApi
    val sentryApi: Shake2ReportApi
}

@SingleIn(FlipperBleServiceGraph::class)
@MergeComponent(
    FlipperBleServiceGraph::class,
    dependencies = [FlipperBleServiceComponentDependencies::class]
)
interface FlipperBleServiceComponent : FlipperBleServiceComponentDependencies {
    val serviceApiImpl: Provider<FlipperServiceApiImpl>

    @Component.Factory
    interface Factory {
        fun create(
            deps: FlipperBleServiceComponentDependencies,
            @BindsInstance context: Context,
            @BindsInstance scope: CoroutineScope,
            @BindsInstance serviceErrorListener: FlipperServiceErrorListener
        ): FlipperBleServiceComponent
    }
}
