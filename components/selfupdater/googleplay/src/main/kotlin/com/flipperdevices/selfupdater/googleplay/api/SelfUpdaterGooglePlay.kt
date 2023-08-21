package com.flipperdevices.selfupdater.googleplay.api

import android.content.Context
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.selfupdater.api.BuildConfig
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val UPDATE_CODE = 228

@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterGooglePlay @Inject constructor(
    private val context: Context,
    private val inAppNotificationStorage: InAppNotificationStorage
) : SelfUpdaterApi, LogTagProvider {

    override val TAG: String get() = "SelfUpdaterGooglePlay"

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(context) }
    private val appUpdateInfoTask = appUpdateManager.appUpdateInfo
    private var updateListener = InstallStateUpdatedListener(::processUpdateListener)

    private fun processUpdateListener(state: InstallState) {
        info { "Current update state $state" }
        if (state.installStatus() != InstallStatus.DOWNLOADED) return

        inAppNotificationStorage.addNotification(
            InAppNotification.UpdateReady(
                action = appUpdateManager::completeUpdate
            )
        )
    }

    private val mutex = Mutex()
    private val progressState = MutableStateFlow(false)
    override fun getState(): StateFlow<Boolean> = progressState.asStateFlow()

    override fun startCheckUpdate(
        scope: CoroutineScope,
        onEndCheck: suspend (SelfUpdateResult) -> Unit
    ) = launchWithLock(mutex, scope, "startCheckUpdate") {
        progressState.emit(true)

        val activity = CurrentActivityHolder.getCurrentActivity()
        if (activity == null) {
            info { "Activity is null, skip update" }
            progressState.emit(false)
            return@launchWithLock
        }

        info { "Process checkout new update" }
        appUpdateManager.registerListener(updateListener)

        val appUpdateInfo = appUpdateInfoTask.await()
        info { "Current state update id ${appUpdateInfo.updateAvailability()}" }

        if (isUpdateAvailable(appUpdateInfo)) {
            info { "New update available, suggest to update" }
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,
                activity,
                UPDATE_CODE
            )
            onEndCheck(SelfUpdateResult.SUCCESS)
        } else {
            info { "No update available" }
            onEndCheck(SelfUpdateResult.NO_UPDATES)
        }
        progressState.emit(false)
    }

    override fun getInstallSourceName() = "Google Play/" + BuildConfig.BUILD_TYPE
    override fun isSelfUpdateCanManualCheck(): Boolean = false

    private fun isUpdateAvailable(appUpdateInfo: AppUpdateInfo): Boolean {
        return appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
    }
}
