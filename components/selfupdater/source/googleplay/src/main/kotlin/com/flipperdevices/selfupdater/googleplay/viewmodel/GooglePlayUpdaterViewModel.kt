package com.flipperdevices.selfupdater.googleplay.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.selfupdater.source.googleplay.R
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

internal const val UPDATE_CODE = 228

class GooglePlayUpdaterViewModel @VMInject constructor(
    private val context: Context,
    private val inAppNotificationStorage: InAppNotificationStorage,
) : ViewModel() {
    private val appUpdateManager = AppUpdateManagerFactory.create(context)
    private val appUpdateInfoTask = appUpdateManager.appUpdateInfo
    private var updateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            //
        }
    }

    private val isShowDialogState = MutableStateFlow(true)
    fun getUpdateState() = isShowDialogState.asStateFlow()

    init {
        showUpdateDialog()
        appUpdateManager.registerListener(updateListener)
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    context as Activity,
                    UPDATE_CODE
                )
            }
        }
    }

    private fun showUpdateDialog() {
        inAppNotificationStorage.addNotification(
            InAppNotification.UpdateReady(
                title = context.getString(R.string.ready_update_title),
                descriptionId = R.string.ready_update_text,
                action = ::startUpdate,
                durationMs = 5000L
            )
        )
    }

    private fun startUpdate() {
        appUpdateManager.completeUpdate()
    }

    fun declineUpdate() {
        viewModelScope.launch {
            appUpdateManager.unregisterListener(updateListener)
            isShowDialogState.emit(false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        appUpdateManager.unregisterListener(updateListener)
    }
}
