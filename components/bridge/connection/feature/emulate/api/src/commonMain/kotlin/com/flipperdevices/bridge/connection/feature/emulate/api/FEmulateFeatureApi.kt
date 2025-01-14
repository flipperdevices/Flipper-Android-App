package com.flipperdevices.bridge.connection.feature.emulate.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.AppEmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.EmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.FlipperAppErrorHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.StartEmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.StopEmulateHelper
import com.flipperdevices.core.data.SemVer

interface FEmulateFeatureApi : FDeviceFeatureApi {
    fun getAppEmulateHelper(): AppEmulateHelper
    fun getEmulateHelper(): EmulateHelper
    fun getFlipperErrorHelper(): FlipperAppErrorHelper
    fun getStartEmulateHelper(): StartEmulateHelper
    fun getStopEmulateHelper(): StopEmulateHelper

    companion object {
        val API_SUPPORTED_REMOTE_EMULATE = SemVer(
            majorVersion = 0,
            minorVersion = 11
        )
    }
}
