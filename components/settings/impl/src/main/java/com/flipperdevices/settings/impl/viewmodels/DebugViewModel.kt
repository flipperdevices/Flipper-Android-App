package com.flipperdevices.settings.impl.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
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
    private val fapInstallationAllApi: FapInstallationAllApi,
    private val fFeatureProvider: FFeatureProvider
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
        viewModelScope.launch {
            settingsDataStore.updateData {
                it.copy(
                    ignore_unsupported_version = ignored
                )
            }

            askRestartApp()
        }
    }

    private fun onSwitchIgnoreUpdaterVersion(alwaysUpdate: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateData {
                it.copy(
                    always_update = alwaysUpdate
                )
            }
        }
    }

    private fun onSwitchIgnoreSubGhzProvisioning(ignoreSubGhzProvisioningOnZeroRegion: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateData {
                it.copy(
                    ignore_subghz_provisioning_on_zero_region = ignoreSubGhzProvisioningOnZeroRegion
                )
            }
        }
    }

    private fun onSwitchSkipAutoSync(skipAutoSync: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateData {
                it.copy(
                    skip_auto_sync_in_debug = skipAutoSync
                )
            }
        }
    }

    private fun restartRpc() {
        viewModelScope.launch {
            val fRestartRpcFeatureApi = fFeatureProvider.getSync<FRestartRpcFeatureApi>() ?: run {
                com.flipperdevices.core.log.error { "#restartRpc could not find FRestartRpcFeatureApi" }
                return@launch
            }
            fRestartRpcFeatureApi.restartRpc()
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
                it.copy(
                    self_updater_debug = enabled
                )
            }
        }
    }

    private fun onSwitchFapHubDev(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateData {
                it.copy(
                    use_dev_catalog = enabled
                )
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
            val fRestartRpcFeatureApi = fFeatureProvider.getSync<FRestartRpcFeatureApi>() ?: run {
                com.flipperdevices.core.log.error { "#restartRpc could not find FRestartRpcFeatureApi" }
                return@launch
            }
            fRestartRpcFeatureApi.restartRpc()
        }
    }
}
