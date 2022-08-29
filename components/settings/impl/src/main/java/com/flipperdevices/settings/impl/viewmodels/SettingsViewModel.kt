package com.flipperdevices.settings.impl.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.debug.api.StressTestApi
import com.flipperdevices.screenstreaming.api.ScreenStreamingApi
import com.flipperdevices.shake2report.api.Shake2ReportApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class SettingsViewModel @VMInject constructor(
    private val dataStoreSettings: DataStore<Settings>,
    private val cicerone: CiceroneGlobal,
    private val shakeToReportApi: Shake2ReportApi,
    val screenStreamingApi: ScreenStreamingApi,
    val stressTestApi: StressTestApi,
    private val applicationParams: ApplicationParams
) : ViewModel() {

    private val settingsState by lazy {
        dataStoreSettings.data.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = Settings.getDefaultInstance()
        )
    }

    fun getState(): StateFlow<Settings> = settingsState

    fun onSwitchDebug(value: Boolean) {
        viewModelScope.launch {
            dataStoreSettings.updateData {
                it.toBuilder()
                    .setEnabledDebugSettings(value)
                    .build()
            }
        }
    }

    fun onSwitchExperimental(value: Boolean) {
        viewModelScope.launch {
            dataStoreSettings.updateData {
                it.toBuilder()
                    .setEnabledExperimentalFunctions(value)
                    .build()
            }
        }
    }

    fun onReportBug(context: Context) {
        val screen = shakeToReportApi.reportBugScreen(context)
        if (screen != null) {
            cicerone.getRouter().navigateTo(screen)
        }
    }

    fun onChangeSelectedTheme(theme: SelectedTheme) {
        viewModelScope.launch {
            dataStoreSettings.updateData {
                it.toBuilder()
                    .setSelectedTheme(theme)
                    .build()
            }
        }
    }

    fun getSelectedTheme(): SelectedTheme {
        return settingsState.value.selectedTheme
    }

    fun onExpertModeActivate() {
        viewModelScope.launch {
            dataStoreSettings.updateData {
                it.toBuilder()
                    .setExpertMode(true)
                    .build()
            }
        }
    }

    fun versionApp() = applicationParams.version
}
