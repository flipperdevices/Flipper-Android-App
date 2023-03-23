package com.flipperdevices.wearable.emulate.impl.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.LifecycleOwner
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.wearable.emulate.impl.model.KeyToEmulate
import com.flipperdevices.wearable.emulate.impl.viewmodel.ConnectionChannelHelper
import com.flipperdevices.wearable.emulate.impl.viewmodel.ConnectionTester
import com.flipperdevices.wearable.emulate.impl.viewmodel.EmulateHelper
import com.flipperdevices.wearable.emulate.impl.viewmodel.EmulateStateListener
import com.flipperdevices.wearable.emulate.impl.viewmodel.FlipperStatusListener
import com.flipperdevices.wearable.emulate.impl.viewmodel.WearEmulateStateMachine
import com.flipperdevices.wearable.setup.api.SetupApi
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component

@ContributesTo(AppGraph::class)
interface WearEmulateComponentDependencies {
    val context: Context
    val application: Application
    val setupApi: SetupApi
    val dataStore: DataStore<Settings>
}

@SingleIn(WearGraph::class)
@MergeComponent(
    WearGraph::class,
    dependencies = [WearEmulateComponentDependencies::class]
)
interface WearEmulateComponent : WearEmulateComponentDependencies {
    val wearEmulateStateMachine: WearEmulateStateMachine
    val connectionTester: ConnectionTester
    val connectionChannelHelper: ConnectionChannelHelper
    val flipperStatusListener: FlipperStatusListener
    val emulateStateListener: EmulateStateListener
    val emulateHelper: EmulateHelper

    @Component.Factory
    interface Factory {
        fun create(
            deps: WearEmulateComponentDependencies,
            @BindsInstance lifecycleOwner: LifecycleOwner,
            @BindsInstance keyToEmulate: KeyToEmulate
        ): WearEmulateComponent
    }
}
