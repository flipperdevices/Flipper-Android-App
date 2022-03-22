package com.flipperdevices.firstpair.impl.storage

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.PairSettings
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@ContributesBinding(AppGraph::class)
class FirstPairStorageImpl @Inject constructor(
    private val pairSettingsStore: DataStore<PairSettings>
) : FirstPairStorage {
    override fun isTosPassed(): Boolean {
        return runBlocking { pairSettingsStore.data.first() }.tosPassed
    }

    override fun isDeviceSelected(): Boolean {
        return runBlocking { pairSettingsStore.data.first() }.pairDevicePass
    }

    override fun markTosPassed(): Unit = runBlocking {
        pairSettingsStore.updateData {
            it.toBuilder()
                .setTosPassed(true)
                .build()
        }
    }

    override fun markDeviceSelected(deviceId: String?): Unit = runBlocking {
        pairSettingsStore.updateData {
            var builder = it.toBuilder()
                .setPairDevicePass(true)

            if (deviceId != null) {
                builder = builder
                    .setDeviceId(deviceId)
            }
            builder.build()
        }
    }
}
