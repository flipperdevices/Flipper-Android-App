package com.flipperdevices.selfupdater.debug.api

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val NOTIFICATION_DELAY_MS = 5000L

@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterDebug @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>,
    private val inAppNotificationStorage: InAppNotificationStorage
) : SelfUpdaterApi, LogTagProvider {
    override val TAG = "SelfUpdaterDebug"

    override suspend fun startCheckUpdate(onEndCheck: suspend (SelfUpdateResult) -> Unit) {
        val isFlagDebug = dataStoreSettings.data.map { it.selfUpdaterDebug }.first()
        if (!isFlagDebug) {
            info { "Self Updater in Debug Mode disable" }
            return
        }

        delay(NOTIFICATION_DELAY_MS)

        val notification = InAppNotification.UpdateReady(
            action = { info { "Self Updater in Debug Mode action" } }
        )
        inAppNotificationStorage.addNotification(notification)
        onEndCheck(SelfUpdateResult.SUCCESS)
    }

    override fun getInstallSourceName() = "Debug"
    override fun isSelfUpdateChecked(): Boolean = true
}
