package com.flipperdevices.bridge.synchronization.impl

import com.flipperdevices.bridge.dao.api.DaoApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.di.SynchronizationComponent
import com.flipperdevices.bridge.synchronization.impl.model.trackProgressAndReturn
import com.flipperdevices.bridge.synchronization.impl.repository.HashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.KeysListingRepository
import com.flipperdevices.bridge.synchronization.impl.repository.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.utils.TaskWithLifecycle
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TmpSynchronization : TaskWithLifecycle(), LogTagProvider {
    override val TAG = "TestSynchronization"

    private val isLaunched = AtomicBoolean(false)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val synchronizationState = MutableStateFlow(SynchronizationState.NOT_STARTED)

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var daoApi: DaoApi

    init {
        ComponentHolder.component<SynchronizationComponent>().inject(this)
    }

    fun requestServiceAndReceive() {
        if (!isLaunched.compareAndSet(false, true)) {
            info { "Synchronization skipped, because we already in synchronization" }
            return
        }
        serviceProvider.provideServiceApi(this) { serviceApi ->
            scope.launch {
                try {
                    synchronizationState.update { SynchronizationState.IN_PROGRESS }
                    launch(serviceApi)
                } finally {
                    isLaunched.compareAndSet(true, false)
                    synchronizationState.update { SynchronizationState.FINISHED }
                    onStop()
                }
            }
        }
        scope.launch {
            onStart()
        }
    }

    fun getSynchronizationState(): StateFlow<SynchronizationState> {
        return synchronizationState
    }

    private suspend fun launch(serviceApi: FlipperServiceApi) {
        val keys = KeysListingRepository().getAllKeys(
            serviceApi.requestApi
        ).trackProgressAndReturn {
            info { "Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
        }
        val hashes = HashRepository().calculateHash(
            serviceApi.requestApi, keys
        ).trackProgressAndReturn {
            info { "Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
        }
        val repository = ManifestRepository()
        val diffWithFlipper = repository.compareWithManifest(hashes)
        daoApi.getKeysApi().updateKeys(
            diffWithFlipper.map {
                FlipperKey(
                    name = it.hashedKey.keyPath.name,
                    fileType = it.hashedKey.keyPath.fileType
                )
            }
        )

        // End synchronization
        repository.saveManifest(hashes)
    }
}
