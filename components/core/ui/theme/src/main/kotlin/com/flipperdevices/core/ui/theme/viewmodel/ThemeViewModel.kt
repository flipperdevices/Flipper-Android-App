package com.flipperdevices.core.ui.theme.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.map
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.theme.di.ThemeComponent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ThemeViewModel : ViewModel() {
    @Inject
    lateinit var dataStoreSettings: DataStore<Settings>

    init {
        ComponentHolder.component<ThemeComponent>().inject(this)
    }

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
