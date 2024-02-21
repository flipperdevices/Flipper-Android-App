package com.flipperdevices.wearable.emulate.handheld.impl.di

import android.content.Context
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.handheld.impl.request.WearableCommandProcessor
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope

@ContributesTo(AppGraph::class)
interface WearServiceComponentDependencies {
    val context: Context
    val flipperServiceProvider: FlipperServiceProvider
    val emulateHelper: EmulateHelper
    val simpleKeyApi: SimpleKeyApi
    val keyParser: KeyParser
}

@SingleIn(WearHandheldGraph::class)
@MergeComponent(
    WearHandheldGraph::class,
    dependencies = [WearServiceComponentDependencies::class]
)
interface WearServiceComponent : WearServiceComponentDependencies {
    val commandInputStream: WearableCommandInputStream<Main.MainRequest>
    val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>
    val commandProcessors: Set<WearableCommandProcessor>

    @Component.Factory
    interface Factory {
        fun create(
            deps: WearServiceComponentDependencies,
            @BindsInstance scope: CoroutineScope
        ): WearServiceComponent
    }
}
