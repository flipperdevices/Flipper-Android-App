package com.flipperdevices.toolstab.impl.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class DebugSettingsViewModel @Inject constructor(
    settingsDataStore: DataStore<Settings>
) : DecomposeViewModel() {
    val showRemoteControls: StateFlow<Boolean> = settingsDataStore.data
        .map { it.show_remote_controls && it.enabled_experimental_functions }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
}
