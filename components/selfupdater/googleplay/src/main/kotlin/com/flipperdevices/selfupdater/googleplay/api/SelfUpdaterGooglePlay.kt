package com.flipperdevices.selfupdater.googleplay.api

import android.content.Context
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.selfupdater.api.BuildConfig
import com.flipperdevices.selfupdater.api.SelfUpdaterSourceApi
import com.flipperdevices.selfupdater.models.SelfUpdateResult
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val UPDATE_CODE = 228

@Singleton
@ContributesBinding(AppGraph::class, SelfUpdaterSourceApi::class)
class SelfUpdaterGooglePlay @Inject constructor(
    private val context: Context,
    private val inAppNotificationStorage: InAppNotificationStorage
) : SelfUpdaterSourceApi, LogTagProvider {

    override val TAG: String get() = "SelfUpdaterGooglePlay"

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(context) }
    private val appUpdateInfoTask = appUpdateManager.appUpdateInfo
    private var updateListener = InstallStateUpdatedListener(::processUpdateListener)

    private fun processUpdateListener(state: InstallState) {
        info { "Current update state $state" }
        val installStatus = state.installStatus()
        info { "Current update status $installStatus" }

        when (installStatus) {
            InstallStatus.DOWNLOADED -> {
                val notification = InAppNotification.SelfUpdateReady(
                    action = appUpdateManager::completeUpdate
                )
                inAppNotificationStorage.addNotification(notification)
            }
            InstallStatus.FAILED, InstallStatus.CANCELED -> {
                val notification = InAppNotification.SelfUpdateError()
                inAppNotificationStorage.addNotification(notification)
            }
            else -> {}
        }
    }

    override suspend fun checkUpdate(manual: Boolean): SelfUpdateResult {
        val activity = CurrentActivityHolder.getCurrentActivity()
            ?: return SelfUpdateResult.ERROR

        info { "Process checkout new update" }
        appUpdateManager.registerListener(updateListener)

        val appUpdateInfo = appUpdateInfoTask.await()
        info { "Current state update id ${appUpdateInfo.updateAvailability()}" }

        return if (isUpdateAvailable(appUpdateInfo)) {
            info { "New update available, suggest to update" }
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,
                activity,
                UPDATE_CODE
            )
            SelfUpdateResult.COMPLETE
        } else {
            SelfUpdateResult.NO_UPDATES
        }
    }

    override fun getInstallSourceName() = "Google Play/" + BuildConfig.BUILD_TYPE
    override fun isSelfUpdateCanManualCheck(): Boolean = false

    private fun isUpdateAvailable(appUpdateInfo: AppUpdateInfo): Boolean {
        return appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
    }
}
