package com.flipperdevices.firstpair.impl.storage

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.preference.pb.PairSettings
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.first

@ContributesBinding(AppGraph::class, FirstPairStorage::class)
class FirstPairStorageImpl @Inject constructor(
    private val pairSettingsStore: DataStore<PairSettings>
) : FirstPairStorage, LogTagProvider {
    override val TAG = "FirstPairStorage"

    override fun isTosPassed(): Boolean {
        return runBlockingWithLog { pairSettingsStore.data.first() }.tosPassed
    }

    override fun isDeviceSelected(): Boolean {
        return runBlockingWithLog { pairSettingsStore.data.first() }.pairDevicePass
    }

    override fun markTosPassed(): Unit = runBlockingWithLog {
        pairSettingsStore.updateData {
            it.toBuilder()
                .setTosPassed(true)
                .build()
        }
    }

    override fun markDeviceSelected(
        deviceId: String?,
        deviceName: String?
    ): Unit = runBlockingWithLog {
        pairSettingsStore.updateData {
            var builder = it.toBuilder()
                .setPairDevicePass(true)

            if (deviceId != null) {
                builder = builder
                    .setDeviceId(deviceId)
            }
            if (deviceName != null) {
                var deviceNameFormatted = deviceName.trim()
                if (deviceNameFormatted.startsWith(Constants.DEVICENAME_PREFIX)) {
                    deviceNameFormatted = deviceNameFormatted
                        .replaceFirst(Constants.DEVICENAME_PREFIX, "")
                        .trim()
                }
                builder = builder
                    .setDeviceName(deviceNameFormatted)
            }
            builder.build()
        }
    }
}
