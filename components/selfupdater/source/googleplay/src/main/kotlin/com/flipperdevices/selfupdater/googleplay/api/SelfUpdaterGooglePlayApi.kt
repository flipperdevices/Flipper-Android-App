package com.flipperdevices.selfupdater.googleplay.api

import android.app.Activity
import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
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
private const val NOTIFICATION_MS = 5000L

@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterGooglePlayApi @Inject constructor(
    private val context: Context,
    private val inAppNotificationStorage: InAppNotificationStorage
) : SelfUpdaterApi {

    private val appUpdateManager = AppUpdateManagerFactory.create(context)
    private val appUpdateInfoTask = appUpdateManager.appUpdateInfo
    private var updateListener = InstallStateUpdatedListener(::processUpdateListener)

    private fun processUpdateListener(state: InstallState) {
        if (state.installStatus() != InstallStatus.DOWNLOADED) return

        inAppNotificationStorage.addNotification(
            InAppNotification.UpdateReady(
                action = appUpdateManager::completeUpdate,
                durationMs = NOTIFICATION_MS
            )
        )
    }

    override fun startCheckUpdate() {
        appUpdateManager.registerListener(updateListener)
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (isUpdateAvailable(appUpdateInfo)) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    context as Activity,
                    UPDATE_CODE
                )
            }
        }
    }

    private fun isUpdateAvailable(appUpdateInfo: AppUpdateInfo): Boolean {
        return appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
    }

    override fun stopProcessCheckUpdate() {
        appUpdateManager.unregisterListener(updateListener)
    }
}
