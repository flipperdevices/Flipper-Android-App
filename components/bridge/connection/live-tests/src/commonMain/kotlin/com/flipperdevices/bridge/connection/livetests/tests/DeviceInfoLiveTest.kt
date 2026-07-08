package com.flipperdevices.bridge.connection.livetests.tests

import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.livetests.api.LiveTest
import com.flipperdevices.bridge.connection.livetests.model.FeatureNotSupportedError
import javax.inject.Inject

class DeviceInfoLiveTest @Inject constructor(
    private val featureProvider: FFeatureProvider
) : LiveTest {
    override val name = "Device info"
    override val description = "Read device name and firmware information over RPC"

    override suspend fun execute(): Result<String> {
        val getInfoApi = featureProvider.getSync<FGetInfoFeatureApi>()
            ?: return Result.failure(FeatureNotSupportedError("GET_INFO"))
        return getInfoApi.get(FGetInfoApiProperty.DeviceInfo.DEVICE_NAME)
            .mapCatching { deviceName ->
                val hardwareVersion = getInfoApi
                    .get(FGetInfoApiProperty.DeviceInfo.HARDWARE_VERSION)
                    .getOrThrow()
                val firmwareBranch = getInfoApi
                    .get(FGetInfoApiProperty.DeviceInfo.FIRMWARE_BRANCH)
                    .getOrThrow()
                "Device '$deviceName', hardware v$hardwareVersion, firmware branch '$firmwareBranch'"
            }
    }
}
