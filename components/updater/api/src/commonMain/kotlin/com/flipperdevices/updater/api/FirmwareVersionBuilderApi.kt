package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion

interface FirmwareVersionBuilderApi {
    fun getFirmwareChannel(branch: String): FirmwareChannel
    fun buildFirmwareVersionFromString(firmwareVersion: String): FirmwareVersion
}
