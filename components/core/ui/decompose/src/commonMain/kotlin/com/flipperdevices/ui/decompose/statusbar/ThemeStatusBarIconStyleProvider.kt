package com.flipperdevices.ui.decompose.statusbar

import androidx.datastore.core.DataStore
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ThemeStatusBarIconStyleProvider(
    private val dataStore: DataStore<Settings>
) : StatusBarIconStyleProvider {

    override fun isStatusBarIconLight(systemIsDark: Boolean): Boolean {
        val settings = runBlocking { dataStore.data.first() }
        return when (settings.selected_theme) {
            SelectedTheme.SYSTEM,
            is SelectedTheme.Unrecognized -> systemIsDark

            SelectedTheme.DARK -> true
            SelectedTheme.LIGHT -> false
        }
    }
}
