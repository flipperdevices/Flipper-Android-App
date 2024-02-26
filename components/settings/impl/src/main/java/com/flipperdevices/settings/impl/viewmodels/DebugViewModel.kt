package com.flipperdevices.settings.impl.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.installation.all.api.FapInstallationAllApi
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.model.DebugSettingAction
import com.flipperdevices.settings.impl.model.DebugSettingSwitch
import com.flipperdevices.settings.impl.model.SettingsNavigationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("TooManyFunctions")
class DebugViewModel @Inject constructor(
    private val application: Application,
    private val synchronizationApi: SynchronizationApi,
    private val settingsDataStore: DataStore<Settings>,
    private val serviceProvider: FlipperServiceProvider,
    private val fapInstallationAllApi: FapInstallationAllApi
) : DecomposeViewModel() {

    fun onAction(
        action: DebugSettingAction,
        navigation: StackNavigation<SettingsNavigationConfig>
    ) {
        when (action) {
            DebugSettingAction.InstallAllFap -> installAllApplication()
            DebugSettingAction.RestartRPC -> restartRpc()
            DebugSettingAction.StartSynchronization -> onStartSynchronization()
            DebugSettingAction.StressTest -> onOpenStressTest(navigation)
            DebugSettingAction.BrokeBytes -> brokeBytes()
        }
    }

    fun onSwitch(switch: DebugSettingSwitch, flag: Boolean) {
        when (switch) {
            DebugSettingSwitch.FapHubDev -> onSwitchFapHubDev(flag)
            DebugSettingSwitch.IgnoreSupportedVersion -> onSwitchIgnoreSupportedVersion(flag)
            DebugSettingSwitch.IgnoreUpdaterVersion -> onSwitchIgnoreUpdaterVersion(flag)
            DebugSettingSwitch.SelfUpdaterDebug -> onSwitchSelfUpdaterDebug(flag)
            DebugSettingSwitch.SkipAutoSync -> onSwitchSkipAutoSync(flag)
            DebugSettingSwitch.SkipProvisioning -> onSwitchIgnoreSubGhzProvisioning(flag)
        }
    }

    private fun onOpenStressTest(navigation: StackNavigation<SettingsNavigationConfig>) {
        navigation.pushToFront(SettingsNavigationConfig.StressTest)
    }

    private fun onStartSynchronization() {
        synchronizationApi.startSynchronization(force = true)
    }

    private fun onSwitchIgnoreSupportedVersion(ignored: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            settingsDataStore.updateData {
                it.toBuilder()
                    .setIgnoreUnsupportedVersion(ignored)
                    .build()
            }

            askRestartApp()
        }
    }

    private fun onSwitchIgnoreUpdaterVersion(alwaysUpdate: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            settingsDataStore.updateData {
                it.toBuilder()
                    .setAlwaysUpdate(alwaysUpdate)
                    .build()
            }
        }
    }

    private fun onSwitchIgnoreSubGhzProvisioning(ignoreSubGhzProvisioningOnZeroRegion: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            settingsDataStore.updateData {
                it.toBuilder()
                    .setIgnoreSubghzProvisioningOnZeroRegion(ignoreSubGhzProvisioningOnZeroRegion)
                    .build()
            }
        }
    }

    private fun onSwitchSkipAutoSync(skipAutoSync: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            settingsDataStore.updateData {
                it.toBuilder()
                    .setSkipAutoSyncInDebug(skipAutoSync)
                    .build()
            }
        }
    }

    private fun restartRpc() {
        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch {
                it.restartRPC()
            }
        }
    }

    private suspend fun askRestartApp() = withContext(Dispatchers.Main) {
        Toast.makeText(
            application,
            R.string.debug_ignored_unsupported_version_toast,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun onSwitchSelfUpdaterDebug(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateData {
                it.toBuilder()
                    .setSelfUpdaterDebug(enabled)
                    .build()
            }
        }
    }

    private fun onSwitchFapHubDev(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateData {
                it.toBuilder()
                    .setUseDevCatalog(enabled)
                    .build()
            }
            askRestartApp()
        }
    }

    private fun installAllApplication() {
        viewModelScope.launch {
            fapInstallationAllApi.installAll()
        }
    }

    private fun brokeBytes() {
        viewModelScope.launch {
            serviceProvider.getServiceApi().requestApi.sendTrashBytesAndBrokeSession()
        }
    }
}
