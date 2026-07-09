package com.flipperdevices.keyemulate.tasks

import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.error
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding

@SingleIn(AppGraph::class)
@ContributesBinding(AppGraph::class, binding<CloseEmulateAppTaskHolder>())
class CloseEmulateAppTaskHolderImpl @Inject constructor(
    private val fFeatureProvider: FFeatureProvider
) : CloseEmulateAppTaskHolder {
    private var closeEmulateAppTask: CloseEmulateAppTask? = null
    private val scope = CoroutineScope(SupervisorJob() + FlipperDispatchers.workStealingDispatcher)

    @Synchronized
    override fun closeEmulateApp() {
        scope.launch {
            val fEmulateApi = fFeatureProvider.getSync<FEmulateFeatureApi>() ?: run {
                error { "#onStartEmulateInternal could not get emulate api" }
                return@launch
            }
            val emulateHelper = fEmulateApi.getEmulateHelper()
            if (closeEmulateAppTask != null) {
                return@launch
            }
            val localCloseEmulateAppTask = CloseEmulateAppTask(emulateHelper)
            closeEmulateAppTask = localCloseEmulateAppTask
            localCloseEmulateAppTask.start(Unit) {
                closeEmulateAppTask = null
            }
        }
    }
}
