package com.flipperdevices.settings.impl.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.debug.api.StressTestFeatureEntry
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.model.ExportState
import com.flipperdevices.shake2report.api.Shake2ReportApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tangle.viewmodel.VMInject

@Suppress("LongParameterList")
class SettingsViewModel @VMInject constructor(
    private val dataStoreSettings: DataStore<Settings>,
    private val cicerone: CiceroneGlobal,
    private val shakeToReportApi: Shake2ReportApi,
    val screenStreamingFeatureEntry: ScreenStreamingFeatureEntry,
    val stressTestFeatureEntry: StressTestFeatureEntry,
    private val applicationParams: ApplicationParams,
    private val exportKeysHelper: ExportKeysHelper
) : ViewModel() {
    private val settingsState by lazy {
        dataStoreSettings.data.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = Settings.getDefaultInstance()
        )
    }
    private val exportStateFlow = MutableStateFlow(ExportState.NOT_STARTED)

    fun getState(): StateFlow<Settings> = settingsState

    fun getExportState(): StateFlow<ExportState> = exportStateFlow

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

    fun onMakeExport(context: Context) {
        if (!exportStateFlow.compareAndSet(ExportState.NOT_STARTED, ExportState.IN_PROGRESS)) {
            return
        }
        viewModelScope.launch {
            val file = exportKeysHelper.createBackupArchive()
            withContext(Dispatchers.Main) {
                ShareHelper.shareFile(context, file, R.string.export_keys_picker_title)
                exportStateFlow.compareAndSet(ExportState.IN_PROGRESS, ExportState.NOT_STARTED)
            }
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
