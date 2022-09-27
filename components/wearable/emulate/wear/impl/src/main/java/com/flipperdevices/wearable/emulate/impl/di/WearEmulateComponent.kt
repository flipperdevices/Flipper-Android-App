package com.flipperdevices.wearable.emulate.impl.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main.MainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main.MainResponse
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component

@ContributesTo(AppGraph::class)
interface WearEmulateComponentDependencies {
    val context: Context
    val application: Application
}

@SingleIn(WearGraph::class)
@MergeComponent(
    WearGraph::class,
    dependencies = [WearEmulateComponentDependencies::class]
)
interface WearEmulateComponent : WearEmulateComponentDependencies {
    val commandInputStream: WearableCommandInputStream<MainResponse>
    val commandOutputStream: WearableCommandOutputStream<MainRequest>

    @Component.Factory
    interface Factory {
        fun create(
            deps: WearEmulateComponentDependencies,
            @BindsInstance lifecycleOwner: LifecycleOwner
        ): WearEmulateComponent
    }
}
