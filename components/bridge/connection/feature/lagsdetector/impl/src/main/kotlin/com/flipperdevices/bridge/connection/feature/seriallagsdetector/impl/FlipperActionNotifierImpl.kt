package com.flipperdevices.bridge.connection.feature.seriallagsdetector.impl

import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class FlipperActionNotifierImpl(
    private val scope: CoroutineScope
) {
    private val actionFlow = MutableSharedFlow<Unit>()

    fun getActionFlow(): Flow<Unit> = actionFlow

    fun notifyAboutAction() {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            actionFlow.emit(Unit)
        }
    }
}
