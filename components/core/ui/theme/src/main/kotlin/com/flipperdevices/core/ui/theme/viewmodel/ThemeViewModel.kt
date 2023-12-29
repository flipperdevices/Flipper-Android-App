package com.flipperdevices.core.ui.theme.viewmodel

import androidx.compose.runtime.Immutable
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.ktx.jre.map
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@Immutable
class ThemeViewModel @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>
) : ViewModel() {

    private val settingsState by lazy {
        dataStoreSettings.data.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = Settings.getDefaultInstance()
        )
    }

    fun getAppTheme(): StateFlow<SelectedTheme> = settingsState.map(viewModelScope) {
        it.selectedTheme
    }
}
