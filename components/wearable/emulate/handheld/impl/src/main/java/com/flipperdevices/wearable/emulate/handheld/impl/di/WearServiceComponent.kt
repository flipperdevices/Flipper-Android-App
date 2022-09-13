package com.flipperdevices.wearable.emulate.handheld.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.wearable.emulate.handheld.impl.service.WearRequestListenerService
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface WearServiceComponent {
    fun inject(service: WearRequestListenerService)
}
