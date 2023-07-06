package com.flipperdevices.bridge.service.impl.delegate

import com.flipperdevices.bridge.api.di.FlipperBleServiceGraph
import com.flipperdevices.bridge.api.manager.delegates.FlipperActionNotifier
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


@ContributesBinding(FlipperBleServiceGraph::class, FlipperActionNotifier::class)
class FlipperActionNotifierImpl @Inject constructor(
    private val scope: CoroutineScope
) : FlipperActionNotifier {
    private val actionFlow = MutableSharedFlow<Unit>()

    override fun getActionFlow(): Flow<Unit> = actionFlow

    override fun notifyAboutAction() {
        scope.launch(Dispatchers.Default) {
            actionFlow.emit(Unit)
        }
    }
}
