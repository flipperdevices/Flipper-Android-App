package com.flipperdevices.bridge.connection

import com.flipperdevices.bridge.connection.di.DesktopAppComponent
import dev.zacsweers.metro.createGraphFactory
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

fun main() {
    val applicationScope = CoroutineScope(
        SupervisorJob() + FlipperDispatchers.workStealingDispatcher
    )
    // Always create the root component outside Compose on the UI thread
    val appComponent = createGraphFactory<DesktopAppComponent.Factory>()
        .create(
            scope = applicationScope
        )

    launch(appComponent)
}
