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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.category.DebugCategory
import com.flipperdevices.settings.impl.composable.category.ExperimentalCategory
import com.flipperdevices.settings.impl.composable.category.ExportKeysCategory
import com.flipperdevices.settings.impl.composable.category.OtherSettingsCategory
import com.flipperdevices.settings.impl.composable.category.ThemeCategory
import com.flipperdevices.settings.impl.composable.category.VersionCategory
import com.flipperdevices.settings.impl.composable.elements.AppBar
import com.flipperdevices.settings.impl.model.NavGraphRoute
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableSettings(
    navController: NavHostController,
    aggregatedFeatures: Set<AggregateFeatureEntry>,
    settingsViewModel: SettingsViewModel = tangleViewModel()
) {
    NavHost(navController = navController, startDestination = NavGraphRoute.Settings.name) {
        composable(route = NavGraphRoute.Settings.name) {
            ComposableCommonSetting(navController, settingsViewModel)
        }
        composable(route = NavGraphRoute.ScreenStreaming.name) {
            settingsViewModel.screenStreamingApi.ProvideScreen()
        }
        composable(route = NavGraphRoute.StressTest.name) {
            settingsViewModel.stressTestApi.StressTestScreen()
        }
        aggregatedFeatures.forEach {
            with(it) {
                navigation(navController)
            }
        }
    }
}

@Composable
fun ComposableCommonSetting(
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    val settings by settingsViewModel.getState().collectAsState()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(LocalPallet.current.background),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AppBar(R.string.options)
        ThemeCategory(settingsViewModel)
        if (settings.expertMode) {
            DebugCategory(settings, navController, settingsViewModel)
        }
        ExperimentalCategory(settings, navController, settingsViewModel)
        ExportKeysCategory(settingsViewModel)
        OtherSettingsCategory(settingsViewModel)
        VersionCategory(
            version = settingsViewModel.versionApp(),
            onActivateExpertMode = settingsViewModel::onExpertModeActivate
        )
    }
}
