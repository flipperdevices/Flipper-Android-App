package com.flipperdevices.selfupdater.debug.api

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.selfupdater.api.SelfUpdaterSourceApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val NOTIFICATION_DEBUG_DELAY_MS = 15000L

@ContributesBinding(AppGraph::class, SelfUpdaterSourceApi::class)
class SelfUpdaterDebug @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>,
    private val inAppNotificationStorage: InAppNotificationStorage
) : SelfUpdaterSourceApi, LogTagProvider {
    override val TAG = "SelfUpdaterDebug"

    override suspend fun checkUpdate(manual: Boolean): SelfUpdateResult {
        val isFlagDebug = dataStoreSettings.data.map { it.self_updater_debug }.first()
        if (!isFlagDebug) {
            info { "Self Updater in Debug Mode disable" }
            return SelfUpdateResult.ERROR
        }

        delay(NOTIFICATION_DEBUG_DELAY_MS)
        return debugNoUpdates()
    }

    @Suppress("UnusedPrivateMember")
    private fun debugSuccessUpdate(): SelfUpdateResult {
        val startUpdateNotification = InAppNotification.SelfUpdateStarted()
        val readyUpdateNotification = InAppNotification.SelfUpdateReady(
            action = { inAppNotificationStorage.addNotification(startUpdateNotification) },
        )
        inAppNotificationStorage.addNotification(readyUpdateNotification)
        return SelfUpdateResult.COMPLETE
    }

    private fun debugNoUpdates(): SelfUpdateResult {
        return SelfUpdateResult.NO_UPDATES
    }

    @Suppress("UnusedPrivateMember")
    private fun debugErrorUpdate(manual: Boolean): SelfUpdateResult {
        if (manual) {
            inAppNotificationStorage.addNotification(InAppNotification.SelfUpdateError())
        }
        return SelfUpdateResult.ERROR
    }

    override fun getInstallSourceName() = "Debug Lorem Ipsum"

    override fun isSelfUpdateCanManualCheck(): Boolean = true
}
