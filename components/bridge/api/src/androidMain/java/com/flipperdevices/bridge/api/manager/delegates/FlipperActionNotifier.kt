package com.flipperdevices.bridge.api.manager.delegates

import kotlinx.coroutines.flow.Flow

interface FlipperActionNotifier {
    fun getActionFlow(): Flow<Unit>

    fun notifyAboutAction()
}
