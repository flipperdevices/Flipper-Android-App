package com.flipperdevices.bridge.synchronization.impl

import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.dao.api.DaoApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.model.trackProgressAndReturn
import com.flipperdevices.bridge.synchronization.impl.repository.HashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.KeysListingRepository
import com.flipperdevices.bridge.synchronization.impl.repository.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.utils.TaskWithLifecycle
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.launch

class SynchronizationTask(
    private val serviceProvider: FlipperServiceProvider,
    private val daoApi: DaoApi
) : TaskWithLifecycle(), LogTagProvider {
    override val TAG = "SynchronizationTask"

    private val taskScope = lifecycleScope

    fun start(onStateUpdate: suspend (SynchronizationState) -> Unit) {
        serviceProvider.provideServiceApi(this) { serviceApi ->
            taskScope.launch {
                try {
                    onStateUpdate(SynchronizationState.IN_PROGRESS)
                    launch(serviceApi)
                } finally {
                    onStateUpdate(SynchronizationState.FINISHED)
                    onStop()
                }
            }
        }
        taskScope.launch {
            onStart()
        }
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
