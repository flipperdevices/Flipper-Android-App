package com.flipperdevices.bridge.synchronization.impl

import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.di.SynchronizationComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.shake2report.api.Shake2ReportApi
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TmpSynchronization : LogTagProvider {
    override val TAG = "TestSynchronization"

    private val isLaunched = AtomicBoolean(false)
    private val synchronizationState = MutableStateFlow(SynchronizationState.NOT_STARTED)
    private var markDirty = false

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var simpleKeyApi: SimpleKeyApi

    @Inject
    lateinit var deleteKeyApi: DeleteKeyApi

    @Inject
    lateinit var utilsKeyApi: UtilsKeyApi

    @Inject
    lateinit var favoriteApi: FavoriteApi

    @Inject
    lateinit var reportApi: Shake2ReportApi

    init {
        ComponentHolder.component<SynchronizationComponent>().inject(this)
    }

    fun requestServiceAndReceive() {
        if (!isLaunched.compareAndSet(false, true)) {
            markDirty = true
            info { "Synchronization skipped, because we already in synchronization" }
            return
        }
        markDirty = false
        val synchronizationTask = SynchronizationTask(
            serviceProvider = serviceProvider,
            simpleKeyApi = simpleKeyApi,
            deleteKeyApi = deleteKeyApi,
            utilsKeyApi = utilsKeyApi,
            favoriteApi = favoriteApi,
            reportApi = reportApi
        )

        synchronizationTask.start { taskState ->
            synchronizationState.update { taskState }
            if (taskState == SynchronizationState.FINISHED) {
                isLaunched.compareAndSet(true, false)
            }
            if (markDirty) {
                requestServiceAndReceive()
            }
        }
    }

    fun getSynchronizationState(): StateFlow<SynchronizationState> {
        return synchronizationState
    }
}
