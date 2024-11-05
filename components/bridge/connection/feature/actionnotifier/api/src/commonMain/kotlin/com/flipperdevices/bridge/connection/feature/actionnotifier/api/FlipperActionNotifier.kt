package com.flipperdevices.bridge.connection.feature.actionnotifier.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface FlipperActionNotifier {
    fun getActionFlow(): Flow<Unit>

    fun notifyAboutAction()

    fun interface Factory {
        fun invoke(scope: CoroutineScope): FlipperActionNotifier
    }
}
