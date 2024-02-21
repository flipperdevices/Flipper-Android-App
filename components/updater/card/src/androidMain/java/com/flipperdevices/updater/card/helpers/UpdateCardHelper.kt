package com.flipperdevices.updater.card.helpers

import com.flipperdevices.core.log.error
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.updater.card.utils.isGreaterThan
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.OfficialFirmware
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdateErrorType
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.model.VersionFiles
import com.flipperdevices.updater.model.WebUpdaterFirmware
import kotlinx.coroutines.Deferred
import java.net.UnknownHostException
import java.util.EnumMap

class UpdateCardHelper(
    private val updateChannel: FirmwareChannel?,
    private val isFlashExist: Boolean?,
    private val firmwareVersion: FirmwareVersion?,
    private val alwaysShowUpdate: Boolean,
    private val webUpdate: Deeplink.BottomBar.DeviceTab.WebUpdate?,
    private val latestVersionAsync: Deferred<Result<EnumMap<FirmwareChannel, VersionFiles>>>
) {

    suspend fun processUpdateCardState(): UpdateCardState {
        if (isFlashExist == null || firmwareVersion == null) return UpdateCardState.InProgress
        if (!isFlashExist) return UpdateCardState.Error(UpdateErrorType.NO_SD_CARD)

        val latestVersionFromNetworkResult = latestVersionAsync.await()

        latestVersionFromNetworkResult.onFailure { exception ->
            return processNetworkException(exception)
        }

        if (webUpdate != null) return processDeeplinkWebUpdater()

        if (updateChannel == FirmwareChannel.CUSTOM) return processUpdateFromFile()

        val latestVersionFromNetwork = latestVersionFromNetworkResult
            .getOrNull()
            ?.get(updateChannel) ?: return processNoUpdate()

        val isUpdateAvailable = alwaysShowUpdate ||
            latestVersionFromNetwork.version.isGreaterThan(firmwareVersion) ?: true ||
            updateChannel == FirmwareChannel.UNKNOWN
        if (isUpdateAvailable) return processUpdateAvailable(latestVersionFromNetwork)

        return processNoUpdate()
    }

    private fun processNetworkException(exception: Throwable): UpdateCardState {
        return if (exception is UnknownHostException) {
            UpdateCardState.Error(UpdateErrorType.NO_INTERNET)
        } else {
            error(exception) { "Error while getting latest version from network" }
            UpdateCardState.Error(UpdateErrorType.UNABLE_TO_SERVER)
        }
    }

    private fun processDeeplinkWebUpdater(): UpdateCardState {
        val webUpdate = checkNotNull(webUpdate)
        val nameSlice = webUpdate.name.split(" ")
        val name: String = if (nameSlice.size > 1) nameSlice[0] else webUpdate.name
        val updateRequest = UpdateRequest(
            updateFrom = checkNotNull(firmwareVersion),
            updateTo = FirmwareVersion(
                channel = FirmwareChannel.CUSTOM,
                version = name
            ),
            changelog = null,
            content = WebUpdaterFirmware(webUpdate.url)
        )
        return UpdateCardState.UpdateAvailable(update = updateRequest, isOtherChannel = true)
    }

    private fun processUpdateFromFile(): UpdateCardState {
        return UpdateCardState.UpdateFromFile(
            flipperVersion = checkNotNull(firmwareVersion),
            updateVersion = FirmwareVersion(channel = FirmwareChannel.CUSTOM, version = "")
        )
    }

    private fun processNoUpdate(): UpdateCardState {
        return UpdateCardState.NoUpdate(checkNotNull(firmwareVersion))
    }

    private fun processUpdateAvailable(latestVersionFromNetwork: VersionFiles): UpdateCardState {
        return UpdateCardState.UpdateAvailable(
            update = UpdateRequest(
                updateFrom = checkNotNull(firmwareVersion),
                updateTo = latestVersionFromNetwork.version,
                changelog = latestVersionFromNetwork.changelog,
                content = OfficialFirmware(latestVersionFromNetwork.updaterFile)
            ),
            isOtherChannel = latestVersionFromNetwork.version.channel
                != firmwareVersion.channel
        )
    }
}
