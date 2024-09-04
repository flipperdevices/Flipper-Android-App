package com.flipperdevices.keyemulate.tasks

import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, CloseEmulateAppTaskHolder::class)
class CloseEmulateAppTaskHolderImpl @Inject constructor() : CloseEmulateAppTaskHolder {
    private var closeEmulateAppTask: CloseEmulateAppTask? = null

    @Synchronized
    override fun closeEmulateApp(
        serviceProvider: FlipperServiceProvider,
        emulateHelper: EmulateHelper
    ) {
        if (closeEmulateAppTask != null) {
            return
        }
        val localCloseEmulateAppTask = CloseEmulateAppTask(serviceProvider, emulateHelper)
        closeEmulateAppTask = localCloseEmulateAppTask
        localCloseEmulateAppTask.start(Unit) {
            closeEmulateAppTask = null
        }
    }
}
