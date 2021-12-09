package com.flipperdevices.bridge.synchronization.impl

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.impl.di.SynchronizationComponent
import com.flipperdevices.bridge.synchronization.impl.model.trackProgressAndReturn
import com.flipperdevices.bridge.synchronization.impl.repository.HashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.KeysListingRepository
import com.flipperdevices.bridge.synchronization.impl.utils.TaskWithLifecycle
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import javax.inject.Inject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TmpSynchronization : TaskWithLifecycle(), LogTagProvider {
    override val TAG = "TestSynchronization"

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<SynchronizationComponent>().inject(this)
    }

    fun requestServiceAndReceive() {
        serviceProvider.provideServiceApi(this) { serviceApi ->
            GlobalScope.launch {
                try {
                    launch(serviceApi)
                } finally {
                    onStop()
                }
            }
        }
        onStart()
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

        info { "Received ${hashes.size}" }
    }
}
