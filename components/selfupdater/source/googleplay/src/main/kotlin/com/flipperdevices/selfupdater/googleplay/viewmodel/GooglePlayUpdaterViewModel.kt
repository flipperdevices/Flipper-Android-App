package com.flipperdevices.selfupdater.googleplay.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateInfo
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
) : ViewModel() {
    private val appUpdateManager = AppUpdateManagerFactory.create(context)
    private val appUpdateInfoTask = appUpdateManager.appUpdateInfo
    private var appUpdateInfo: AppUpdateInfo? = null
    private var updateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            viewModelScope.launch {
                updateState.emit(UpdateState.DOWNLOADED)
            }
        }
    }

    private val updateState = MutableStateFlow(UpdateState.NONE)
    fun getUpdateState() = updateState.asStateFlow()

    init {
        appUpdateManager.registerListener(updateListener)
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                this.appUpdateInfo = appUpdateInfo
                viewModelScope.launch {
                    updateState.emit(UpdateState.AVAILABLE)
                }
            }
        }
    }

    fun requestDownloadUpdate() {
        val appUpdateInfo = appUpdateInfo ?: return
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.FLEXIBLE,
            context as Activity,
            UPDATE_CODE
        )
    }

    fun startUpdate() {
        appUpdateManager.completeUpdate()
    }

    fun declineUpdate() {
        viewModelScope.launch {
            appUpdateManager.unregisterListener(updateListener)
            updateState.emit(UpdateState.NONE)
        }
    }

    override fun onCleared() {
        super.onCleared()
        appUpdateManager.unregisterListener(updateListener)
    }
}

enum class UpdateState {
    AVAILABLE,
    DOWNLOADED,
    NONE
}
