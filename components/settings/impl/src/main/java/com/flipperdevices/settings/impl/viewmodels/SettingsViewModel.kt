package com.flipperdevices.settings.impl.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.model.ExportState
import com.flipperdevices.shake2report.api.Shake2ReportApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>,
    private val exportKeysHelper: ExportKeysHelper,
    private val shake2ReportApi: Shake2ReportApi,
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "SettingsViewModel"

    private val settingsStateFlow = MutableStateFlow(Settings.getDefaultInstance())
    private val exportStateFlow = MutableStateFlow(ExportState.NOT_STARTED)

    init {
        dataStoreSettings.data.onEach {
            settingsStateFlow.emit(it)
        }.launchIn(viewModelScope)
    }

    fun getState() = settingsStateFlow.asStateFlow()

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
