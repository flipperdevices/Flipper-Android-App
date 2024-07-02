package com.flipperdevices.bridge.service.impl.model

import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.core.preference.pb.PairSettings

data class SavedFlipperConnectionInfo private constructor(
    val id: String,
    val name: String?
) {
    companion object {
        fun build(
            pairSettings: PairSettings
        ): SavedFlipperConnectionInfo? {
            if (pairSettings.deviceId.isNullOrBlank()) {
                return null
            }
            val flipperName = if (pairSettings.deviceName.startsWith(Constants.DEVICENAME_PREFIX)) {
                pairSettings.deviceName
            } else {
                "${Constants.DEVICENAME_PREFIX} ${pairSettings.deviceName}"
            }

            return SavedFlipperConnectionInfo(
                id = pairSettings.deviceId,
                name = flipperName
            )
        }
    }
}
