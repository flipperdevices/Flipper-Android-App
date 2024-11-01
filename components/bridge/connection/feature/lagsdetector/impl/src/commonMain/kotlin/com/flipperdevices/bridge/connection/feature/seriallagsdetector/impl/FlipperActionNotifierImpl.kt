package com.flipperdevices.bridge.connection.feature.seriallagsdetector.impl

import com.flipperdevices.bridge.connection.feature.seriallagsdetector.api.FlipperActionNotifier
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, FlipperActionNotifier::class)
class FlipperActionNotifierImpl @Inject constructor() : FlipperActionNotifier {
    private val actionFlow = MutableSharedFlow<Unit>()

    override fun getActionFlow(): Flow<Unit> = actionFlow

    override suspend fun notifyAboutAction(): Unit = coroutineScope {
        launch(FlipperDispatchers.workStealingDispatcher) {
            actionFlow.emit(Unit)
        }
    }
}
