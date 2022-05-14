package com.flipperdevices.settings.impl.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.settings.impl.di.SettingsComponent
import com.flipperdevices.shake2report.api.Shake2ReportApi
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    @Inject
    lateinit var dataStoreSettings: DataStore<Settings>

    @Inject
    lateinit var cicerone: CiceroneGlobal

    @Inject
    lateinit var shakeToReportApi: Shake2ReportApi

    init {
        ComponentHolder.component<SettingsComponent>().inject(this)
    }

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
}
