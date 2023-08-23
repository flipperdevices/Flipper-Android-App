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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

private const val NOTIFICATION_DELAY_MS = 20000L

@Singleton
@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterDebug @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>,
    private val inAppNotificationStorage: InAppNotificationStorage
) : SelfUpdaterApi, LogTagProvider {
    override val TAG = "SelfUpdaterDebug"

    private val mutex = Mutex()

    override suspend fun startCheckUpdate(onEndCheck: suspend (SelfUpdateResult) -> Unit) {
        if (mutex.isLocked) {
            info { "Update already in progress" }
            onEndCheck(SelfUpdateResult.IN_PROGRESS)
            return
        }
        mutex.withLock {
            info { "#startCheckUpdate" }
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
    }

    override fun getInstallSourceName() = "Debug Lorem Ipsum"

    override fun isSelfUpdateCanManualCheck(): Boolean = true
}
