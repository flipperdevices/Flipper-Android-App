package com.flipperdevices.bridge.connection.feature.seriallagsdetector.impl

import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, FlipperActionNotifier.Factory::class)
class FlipperActionNotifierImpl @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope
) : FlipperActionNotifier {
    private val actionFlow = MutableSharedFlow<Unit>()

    override fun getActionFlow(): Flow<Unit> = actionFlow

    override fun notifyAboutAction() {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            actionFlow.emit(Unit)
        }
    }
}
