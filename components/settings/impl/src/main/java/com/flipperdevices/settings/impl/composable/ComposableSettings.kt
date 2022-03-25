package com.flipperdevices.settings.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.category.DebugCategory
import com.flipperdevices.settings.impl.composable.category.ExperimentalCategory
import com.flipperdevices.settings.impl.composable.category.GeneralCategory
import com.flipperdevices.settings.impl.composable.elements.Category
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableSettings(
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val settings by settingsViewModel.getState().collectAsState()
    val systemUiController = rememberSystemUiController()
    val appBarColor = colorResource(DesignSystem.color.background)

    SideEffect {
        systemUiController.setStatusBarColor(appBarColor)
    }

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .background(appBarColor)
    ) {
        Category(R.string.category_general)

        GeneralCategory(
            settings = settings,
            onSwitchExperimental = settingsViewModel::onSwitchExperimental,
            onSwitchDebug = settingsViewModel::onSwitchDebug
        )

        if (settings.enabledExperimentalFunctions) {
            Category(titleId = R.string.category_experimental)
            ExperimentalCategory()
        }

        if (settings.enabledDebugSettings) {
            Category(titleId = R.string.category_debug)
            DebugCategory()
        }
    }
}
