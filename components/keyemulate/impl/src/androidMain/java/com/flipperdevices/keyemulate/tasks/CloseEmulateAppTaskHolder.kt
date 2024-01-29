package com.flipperdevices.keyemulate.tasks

import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.keyemulate.api.EmulateHelper

object CloseEmulateAppTaskHolder {
    private var closeEmulateAppTask: CloseEmulateAppTask? = null

    @Synchronized
    fun closeEmulateApp(serviceProvider: FlipperServiceProvider, emulateHelper: EmulateHelper) {
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
