package com.flipperdevices.wearable.emulate.impl.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.wearable.emulate.impl.viewmodel.ConnectionChannelHelper
import com.flipperdevices.wearable.emulate.impl.viewmodel.WearEmulateStateMachine
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
    val wearEmulateStateMachine: WearEmulateStateMachine
    val connectionChannelHelper: ConnectionChannelHelper

    @Component.Factory
    interface Factory {
        fun create(
            deps: WearEmulateComponentDependencies,
            @BindsInstance lifecycleOwner: LifecycleOwner
        ): WearEmulateComponent
    }
}
