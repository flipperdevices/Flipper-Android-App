package com.flipperdevices.wearable.emulate.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.wearable.setup.api.SetupApi
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface WearEmulateComponent {
    val setupApi: SetupApi
}
