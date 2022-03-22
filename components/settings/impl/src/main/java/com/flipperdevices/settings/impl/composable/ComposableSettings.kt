package com.flipperdevices.settings.impl.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.category.DebugCategory
import com.flipperdevices.settings.impl.composable.category.ExperimentalCategory
import com.flipperdevices.settings.impl.composable.category.GeneralCategory
import com.flipperdevices.settings.impl.composable.elements.Category
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableSettings(
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val settings by settingsViewModel.getState().collectAsState()

    Row {
        Category(R.string.category_debug)

        GeneralCategory(
            settings = settings,
            onSwitchExperimental = settingsViewModel::onSwitchExperimental,
            onSwitchDebug = settingsViewModel::onSwitchDebug
        )

        if (settings.enabledDebugSettings) {
            Category(titleId = R.string.general_debug_title)
            DebugCategory()
        }

        if (settings.enabledExperimentalFunctions) {
            Category(titleId = R.string.general_experimental_title)
            ExperimentalCategory()
        }
    }
}
