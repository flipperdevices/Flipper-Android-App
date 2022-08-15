package com.flipperdevices.updater.impl.utils

import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion

private const val DEVICE_VERSION_PART_COUNT = 4
private const val DEVICE_VERSION_COMMIT_INDEX = 0
private const val DEVICE_VERSION_TYPE_INDEX = 1
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

        if (typeVersion.trim() == Constants.FirmwareCommit.DEV) {
            return FirmwareVersion(FirmwareChannel.DEV, hash, date)
        }

        if (Constants.FirmwareCommit.RC_REGEX.toRegex() matches typeVersion) {
            return FirmwareVersion(
                FirmwareChannel.RELEASE_CANDIDATE,
                typeVersion.replace(
                    "-${Constants.FirmwareCommit.RC}",
                    ""
                ),
                date
            )
        }

        if (Constants.FirmwareCommit.RELEASE_REGEX.toRegex() matches typeVersion) {
            return FirmwareVersion(
                FirmwareChannel.RELEASE,
                typeVersion,
                date
            )
        }

        return FirmwareVersion(
            FirmwareChannel.UNKNOWN,
            typeVersion,
            date
        )
    }
}
