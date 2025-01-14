package com.flipperdevices.wearable.emulate.handheld.impl.di

import android.content.Context
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.handheld.impl.request.WearableCommandProcessor
import com.squareup.anvil.annotations.ContributesTo
import kotlinx.coroutines.CoroutineScope

@ContributesTo(AppGraph::class)
interface WearServiceComponentDependencies {
    val context: Context
    val fFeatureProvider: FFeatureProvider
    val fDeviceOrchestrator: FDeviceOrchestrator
    val simpleKeyApi: SimpleKeyApi
    val keyParser: KeyParser
}

interface WearServiceComponent {
    val commandInputStream: WearableCommandInputStream<Main.MainRequest>
    val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>
    val commandProcessors: Set<WearableCommandProcessor>

    /**
     * This [ManualFactory] is required to escape from usage of kapt inside this module.
     *
     * [ManualFactory.create] will return manually created [WearServiceComponent] instance
     */
    object ManualFactory {
        fun create(
            deps: WearServiceComponentDependencies,
            scope: CoroutineScope
        ): WearServiceComponent = WearServiceComponentImpl(
            deps = deps,
            scope = scope
        )
    }
}
