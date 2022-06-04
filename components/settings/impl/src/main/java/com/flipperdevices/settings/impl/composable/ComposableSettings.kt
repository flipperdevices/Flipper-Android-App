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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.category.BugReportCategory
import com.flipperdevices.settings.impl.composable.category.DebugCategory
import com.flipperdevices.settings.impl.composable.category.ExperimentalCategory
import com.flipperdevices.settings.impl.composable.category.VersionCategory
import com.flipperdevices.settings.impl.composable.elements.AppBar
import com.flipperdevices.settings.impl.model.NavGraphRoute
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ComposableSettings(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    /*
        When navigate from Options to another screen we have accent color for status bar
        rememberSystemUiController changed color
     */
    NavHost(navController = navController, startDestination = NavGraphRoute.Settings.name) {
        composable(route = NavGraphRoute.Settings.name) {
            rememberSystemUiController().setStatusBarColor(
                color = colorResource(id = DesignSystem.color.accent)
            )
            ComposableCommonSetting(navController, settingsViewModel)
        }
        composable(route = NavGraphRoute.ScreenStreaming.name) {
            rememberSystemUiController().setStatusBarColor(
                color = colorResource(id = DesignSystem.color.background)
            )
            settingsViewModel.screenStreamingApi.ProvideScreen()
        }
        composable(route = NavGraphRoute.StressTest.name) {
            rememberSystemUiController().setStatusBarColor(
                color = colorResource(id = DesignSystem.color.background)
            )
            settingsViewModel.stressTestApi.StressTestScreen()
        }
    }
}

@Composable
fun ComposableCommonSetting(
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val settings by settingsViewModel.getState().collectAsState()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(colorResource(DesignSystem.color.background)),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AppBar(R.string.options)
        DebugCategory(settings, navController, settingsViewModel)
        ExperimentalCategory(settings, navController, settingsViewModel)
        BugReportCategory(onClick = { settingsViewModel.onReportBug(context) })
        VersionCategory()
    }
}
