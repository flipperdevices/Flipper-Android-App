package com.flipperdevices.selfupdater.debug.api

import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

private const val NOTIFICATION_DELAY_MS = 5000L

@Singleton
@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterDebug @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>,
    private val inAppNotificationStorage: InAppNotificationStorage
) : SelfUpdaterApi, LogTagProvider {
    override val TAG = "SelfUpdaterDebug"

    private val mutex = Mutex()
    private val progressState = MutableStateFlow(false)
    override fun getState(): StateFlow<Boolean> = progressState.asStateFlow()

    override fun startCheckUpdate(
        scope: CoroutineScope,
        onEndCheck: suspend (SelfUpdateResult) -> Unit
    ) = launchWithLock(
        mutex = mutex,
        scope = scope,
        tag = "startCheckUpdate"
    ) {
        progressState.emit(true)
        val isFlagDebug = dataStoreSettings.data.map { it.selfUpdaterDebug }.first()
        if (!isFlagDebug) {
            info { "Self Updater in Debug Mode disable" }
            progressState.emit(false)
            return@launchWithLock
        }

        delay(NOTIFICATION_DELAY_MS * 2)

        val notification = InAppNotification.UpdateReady(
            action = { info { "Self Updater in Debug Mode action" } }
        )
        inAppNotificationStorage.addNotification(notification)
        progressState.emit(false)
        onEndCheck(SelfUpdateResult.NO_UPDATES)
    }

    override fun getInstallSourceName() = "Debug Lorem Ipsum"

    override fun isSelfUpdateCanManualCheck(): Boolean = true
}
