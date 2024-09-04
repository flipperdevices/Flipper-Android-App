package com.flipperdevices.core.ui.theme.viewmodel

import androidx.compose.runtime.Immutable
import androidx.datastore.core.DataStore
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@Immutable
class ThemeViewModel @Inject constructor(
    private val dataStoreSettings: DataStore<Settings>
) : DecomposeViewModel() {
    private val selectedThemeStateFlow = MutableStateFlow<SelectedTheme>(SelectedTheme.SYSTEM)

    init {
        dataStoreSettings.data.onEach {
            selectedThemeStateFlow.emit(it.selected_theme)
        }.launchIn(viewModelScope)
    }

    fun getAppTheme() = selectedThemeStateFlow.asStateFlow()
}
