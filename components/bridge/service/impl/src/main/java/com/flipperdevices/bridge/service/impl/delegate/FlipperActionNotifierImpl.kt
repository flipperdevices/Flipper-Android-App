package com.flipperdevices.bridge.service.impl.delegate

import com.flipperdevices.bridge.api.di.FlipperBleServiceGraph
import com.flipperdevices.bridge.api.manager.delegates.FlipperActionNotifier
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SingleIn(FlipperBleServiceGraph::class)
@ContributesBinding(FlipperBleServiceGraph::class, FlipperActionNotifier::class)
class FlipperActionNotifierImpl @Inject constructor(
    private val scope: CoroutineScope
) : FlipperActionNotifier {
    private val actionFlow = MutableSharedFlow<Unit>()

    override fun getActionFlow(): Flow<Unit> = actionFlow

    override fun notifyAboutAction() {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            actionFlow.emit(Unit)
        }
    }
}
