package com.flipperdevices.updater.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.impl.service.UpdaterWorkManager
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface UpdaterComponent {
    fun inject(updaterWorkManager: UpdaterWorkManager)
}
