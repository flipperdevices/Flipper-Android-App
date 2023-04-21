package com.flipperdevices.updater.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.api.FirmwareVersionBuilderApi
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

private const val DEVICE_VERSION_PART_COUNT = 4
private const val DEVICE_VERSION_COMMIT_INDEX = 0
private const val DEVICE_VERSION_TYPE_INDEX = 1
private const val DEVICE_VERSION_TYPE_DEV = "dev-cfw"
private const val DEVICE_VERSION_TYPE_RELEASE_CANDIDATE = "rc-cfw"
private const val DEVICE_VERSION_TYPE_RELEASE = "release-cfw"
private const val DEVICE_VERSION_DATE_INDEX = 3

@ContributesBinding(AppGraph::class, FirmwareVersionBuilderApi::class)
class FirmwareVersionBuilderApiImpl @Inject constructor() : FirmwareVersionBuilderApi {
    override fun getFirmwareChannel(branch: String): FirmwareChannel {
        val preparedBranch = branch.trim().lowercase()
        if (preparedBranch == DEVICE_VERSION_TYPE_DEV) {
            return FirmwareChannel.DEV
        }

        if (preparedBranch == DEVICE_VERSION_TYPE_RELEASE_CANDIDATE) {
            return FirmwareChannel.RELEASE_CANDIDATE
        }

        if (preparedBranch == DEVICE_VERSION_TYPE_RELEASE) {
            return FirmwareChannel.RELEASE
        }

        return FirmwareChannel.UNKNOWN
    }

    override fun buildFirmwareVersionFromString(
        firmwareVersion: String
    ): FirmwareVersion? {
        val unparsedArray = firmwareVersion.split(" ").filterNot { it.isBlank() }
        if (unparsedArray.size < DEVICE_VERSION_PART_COUNT) {
            return null
        }
        val hash = unparsedArray[DEVICE_VERSION_COMMIT_INDEX]
        val typeVersion = unparsedArray[DEVICE_VERSION_TYPE_INDEX]
        val date = unparsedArray[DEVICE_VERSION_DATE_INDEX]

        return when (val channel = getFirmwareChannel(typeVersion)) {
            FirmwareChannel.DEV -> FirmwareVersion(
                channel = channel,
                version = hash,
                buildDate = date
            )
            FirmwareChannel.RELEASE_CANDIDATE,
            FirmwareChannel.RELEASE,
            FirmwareChannel.UNKNOWN,
            FirmwareChannel.CUSTOM -> FirmwareVersion(
                channel = channel,
                version = typeVersion,
                buildDate = date
            )
        }
    }
}
