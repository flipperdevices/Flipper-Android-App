package com.flipperdevices.info.impl.model

import com.flipperdevices.deeplink.model.Deeplink
import kotlinx.serialization.Serializable

@Serializable
sealed class DeviceScreenNavigationConfig {
    @Serializable
    data class Update(
        val deeplink: Deeplink.BottomBar.DeviceTab.WebUpdate?
    ) : DeviceScreenNavigationConfig()

    @Serializable
    data object FullInfo : DeviceScreenNavigationConfig()

    @Serializable
    data object Options : DeviceScreenNavigationConfig()
}
