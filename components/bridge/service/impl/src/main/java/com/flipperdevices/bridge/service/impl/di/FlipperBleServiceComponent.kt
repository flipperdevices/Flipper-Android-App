package com.flipperdevices.bridge.service.impl.di

import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener
import com.flipperdevices.bridge.api.manager.FlipperReadyListener
import com.flipperdevices.bridge.api.scanner.FlipperScanner
import com.flipperdevices.bridge.impl.manager.service.FlipperVersionApiImpl
import com.flipperdevices.bridge.service.impl.FlipperServiceApiImpl
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.flipperdevices.unhandledexception.api.UnhandledExceptionApi
import com.squareup.anvil.annotations.ContributesTo
import kotlinx.coroutines.CoroutineScope
import javax.inject.Provider

@ContributesTo(AppGraph::class)
interface FlipperBleServiceComponentDependencies {
    val pairSettingsStore: DataStore<PairSettings>
    val settingsStore: DataStore<Settings>
    val metricApi: MetricApi
    val sentryApi: Shake2ReportApi
    val bluetoothScanner: FlipperScanner
    val bluetoothAdapter: BluetoothAdapter
    val flipperReadyListeners: Set<FlipperReadyListener>
    val unhandledExceptionApi: UnhandledExceptionApi
    val flipperVersionApiImpl: FlipperVersionApiImpl
}

interface FlipperBleServiceComponent {
    val serviceApiImpl: Provider<FlipperServiceApiImpl>

    object ManualFactory {
        fun create(
            deps: FlipperBleServiceComponentDependencies,
            context: Context,
            scope: CoroutineScope,
            serviceErrorListener: FlipperServiceErrorListener
        ): FlipperBleServiceComponent = ComponentHolder.getOrCreate {
            FlipperBleServiceComponentImpl(
                deps = deps,
                context = context,
                scope = scope,
                serviceErrorListener = serviceErrorListener
            )
        }
    }
}
