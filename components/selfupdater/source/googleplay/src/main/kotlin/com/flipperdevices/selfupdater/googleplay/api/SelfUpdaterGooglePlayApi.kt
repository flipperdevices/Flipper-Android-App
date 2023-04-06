package com.flipperdevices.selfupdater.googleplay.api

import android.app.Activity
import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.selfupdater.api.BuildConfig
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

private const val UPDATE_CODE = 228

@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterGooglePlayApi @Inject constructor(
    private val context: Context,
    private val inAppNotificationStorage: InAppNotificationStorage
) : SelfUpdaterApi, LogTagProvider {

    override val TAG: String get() = "SelfUpdaterGooglePlayApi"

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

    override suspend fun startCheckUpdateAsync(activity: Activity) {
        info { "Process checkout new update" }
        appUpdateManager.registerListener(updateListener)
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            info { "Current state update id ${appUpdateInfo.updateAvailability()}" }
            if (isUpdateAvailable(appUpdateInfo)) {
                info { "New update available, suggest to update" }
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    activity,
                    UPDATE_CODE
                )
            }
        }
    }

    override fun getInstallSourceName() = "Google Play/" + BuildConfig.BUILD_TYPE

    private fun isUpdateAvailable(appUpdateInfo: AppUpdateInfo): Boolean {
        return appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
    }
}
