package com.flipperdevices.settings.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.category.DebugCategory
import com.flipperdevices.settings.impl.composable.category.ExperimentalCategory
import com.flipperdevices.settings.impl.composable.category.ExportKeysCategory
import com.flipperdevices.settings.impl.composable.category.OtherSettingsCategory
import com.flipperdevices.settings.impl.composable.category.ThemeCategory
import com.flipperdevices.settings.impl.composable.category.VersionCategory
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableCommonSetting(
    navController: NavController,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = tangleViewModel()
) {
    val settings by settingsViewModel.getState().collectAsState()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .background(LocalPallet.current.background),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OrangeAppBar(R.string.options, onBack = navController::popBackStack)
        ThemeCategory(settingsViewModel)
        if (settings.expertMode) {
            DebugCategory(settings, navController, settingsViewModel)
        }
        ExperimentalCategory(settings, navController, settingsViewModel)
        ExportKeysCategory(settingsViewModel)
        OtherSettingsCategory(settingsViewModel, navController)
        VersionCategory(
            version = settingsViewModel.versionApp(),
            onActivateExpertMode = settingsViewModel::onExpertModeActivate
        )
    }
}
