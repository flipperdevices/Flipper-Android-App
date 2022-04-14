package com.flipperdevices.updater.impl.utils

import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion

private const val DEVICE_VERSION_PART_COUNT = 4
private const val DEVICE_VERSION_COMMIT_INDEX = 0
private const val DEVICE_VERSION_TYPE_INDEX = 1
private const val DEVICE_VERSION_TYPE_DEV = "dev"
private const val DEVICE_VERSION_TYPE_RC = "rc"
private const val DEVICE_VERSION_DATE_INDEX = 3

object FirmwareVersionBuildHelper {
    fun buildFirmwareVersionFromString(
        firmwareVersion: String
    ): FirmwareVersion? {
        val unparsedArray = firmwareVersion.split(" ").filterNot { it.isBlank() }
        if (unparsedArray.size < DEVICE_VERSION_PART_COUNT) {
            return null
        }
        val hash = unparsedArray[DEVICE_VERSION_COMMIT_INDEX]
        val typeVersion = unparsedArray[DEVICE_VERSION_TYPE_INDEX]
        val date = unparsedArray[DEVICE_VERSION_DATE_INDEX]

        if (typeVersion.trim() == DEVICE_VERSION_TYPE_DEV) {
            return FirmwareVersion(FirmwareChannel.DEV, hash, date)
        }

        if (typeVersion.contains(DEVICE_VERSION_TYPE_RC)) {
            return FirmwareVersion(
                FirmwareChannel.RELEASE_CANDIDATE,
                typeVersion.replace(
                    "-$DEVICE_VERSION_TYPE_RC",
                    ""
                ),
                date
            )
        }

        return FirmwareVersion(
            FirmwareChannel.RELEASE,
            typeVersion,
            date
        )
    }
}
