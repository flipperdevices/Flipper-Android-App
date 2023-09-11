package com.flipperdevices.settings.impl.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.model.ExportState
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.flipperdevices.shake2report.api.Shake2ReportFeatureEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tangle.viewmodel.VMInject

class SettingsViewModel @VMInject constructor(
    private val dataStoreSettings: DataStore<Settings>,
    private val exportKeysHelper: ExportKeysHelper,
    private val shake2ReportFeatureEntry: Shake2ReportFeatureEntry,
    private val shake2ReportApi: Shake2ReportApi,
) : ViewModel(), LogTagProvider {
    override val TAG = "SettingsViewModel"

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

    fun getShake2ReportInitializationState(): StateFlow<Boolean> = shake2ReportApi.isInitialized()

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

    fun onReportBug(navController: NavController) {
        navController.navigate(shake2ReportFeatureEntry.start())
    }

    fun onMakeExport(context: Context) {
        if (!exportStateFlow.compareAndSet(ExportState.NOT_STARTED, ExportState.IN_PROGRESS)) {
            return
        }
        viewModelScope.launch {
            val file = try {
                exportKeysHelper.createBackupArchive()
            } catch (zipException: Exception) {
                error(zipException) { "Failed create backup" }
                null
            } ?: return@launch
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
}
