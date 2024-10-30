package com.flipperdevices.bridge.connection.feature.seriallagsdetector.api

import kotlinx.coroutines.flow.Flow


interface FlipperActionNotifier {
    fun getActionFlow(): Flow<Unit>

    suspend fun notifyAboutAction()
}
