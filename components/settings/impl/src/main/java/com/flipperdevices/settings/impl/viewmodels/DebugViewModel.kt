package com.flipperdevices.settings.impl.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.settings.impl.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tangle.viewmodel.VMInject

class DebugViewModel @VMInject constructor(
    application: Application,
    private val cicerone: CiceroneGlobal,
    private val firstPairApi: FirstPairApi,
    private val synchronizationApi: SynchronizationApi,
    private val settingsDataStore: DataStore<Settings>
) : AndroidViewModel(application) {

    fun onStartSynchronization() {
        synchronizationApi.startSynchronization(force = true)
    }

    fun onOpenConnectionScreen() {
        cicerone.getRouter().navigateTo(firstPairApi.getFirstPairScreen())
    }

    fun onSwitchIgnoreSupportedVersion(ignored: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            settingsDataStore.updateData {
                it.toBuilder()
                    .setIgnoreUnsupportedVersion(ignored)
                    .build()
            }

            askRestartApp()
        }
    }

    fun onSwitchIgnoreUpdaterVersion(alwaysUpdate: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            settingsDataStore.updateData {
                it.toBuilder()
                    .setAlwaysUpdate(alwaysUpdate)
                    .build()
            }
        }
    }

    fun onSwitchShakeToReport(shakeToReport: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            settingsDataStore.updateData {
                it.toBuilder()
                    .setShakeToReport(shakeToReport)
                    .build()
            }
            askRestartApp()
        }
    }

    private suspend fun askRestartApp() = withContext(Dispatchers.Main) {
        val context = getApplication<Application>()
        Toast.makeText(
            context,
            R.string.debug_ignored_unsupported_version_toast,
            Toast.LENGTH_LONG
        ).show()
    }
}
