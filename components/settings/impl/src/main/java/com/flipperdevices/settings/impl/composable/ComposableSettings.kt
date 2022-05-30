package com.flipperdevices.settings.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.category.DebugCategory
import com.flipperdevices.settings.impl.composable.category.ExperimentalCategory
import com.flipperdevices.settings.impl.composable.category.GeneralCategory
import com.flipperdevices.settings.impl.composable.elements.Category
import com.flipperdevices.settings.impl.composable.elements.SimpleElement
import com.flipperdevices.settings.impl.model.NavGraphRoute
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel

@Composable
fun ComposableSettings(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = viewModel()
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
    }
}

@Composable
fun ComposableCommonSetting(
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    val settings by settingsViewModel.getState().collectAsState()

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .background(colorResource(DesignSystem.color.background))
    ) {
        Category(R.string.category_general)

        GeneralCategory(
            settings = settings,
            onSwitchExperimental = settingsViewModel::onSwitchExperimental,
            onSwitchDebug = settingsViewModel::onSwitchDebug
        )

        if (settings.enabledExperimentalFunctions) {
            Category(titleId = R.string.category_experimental)
            ExperimentalCategory(settings, navController)
        }

        if (settings.enabledDebugSettings) {
            Category(titleId = R.string.category_debug)
            DebugCategory(settings, navController)
        }

        val context = LocalContext.current
        SimpleElement(
            titleId = R.string.debug_shake2report_open,
            onClick = { settingsViewModel.onReportBug(context) }
        )
    }
}
