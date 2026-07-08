package com.flipperdevices.bridge.connection

import com.flipperdevices.bridge.connection.di.DaggerMergedDesktopAppComponent
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun main() {
    val applicationScope = CoroutineScope(
        SupervisorJob() + FlipperDispatchers.workStealingDispatcher
    )
    // Always create the root component outside Compose on the UI thread
    val appComponent = DaggerMergedDesktopAppComponent.factory()
        .create(
            scope = applicationScope
        )

    applicationScope.launch {
        appComponent.rootLiveTest.awaitDeviceAndRunAll()
    }

    launch(appComponent)
}
