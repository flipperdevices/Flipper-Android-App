package com.flipperdevices.keyscreen.impl.tasks

import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider

object CloseEmulateAppTaskHolder {
    private var closeEmulateAppTask: CloseEmulateAppTask? = null

    @Synchronized
    fun closeEmulateApp(serviceProvider: FlipperServiceProvider) {
        if (closeEmulateAppTask != null) {
            return
        }
        val localCloseEmulateAppTask = CloseEmulateAppTask(serviceProvider)
        closeEmulateAppTask = localCloseEmulateAppTask
        localCloseEmulateAppTask.start(Unit) {
            closeEmulateAppTask = null
        }
    }
}
