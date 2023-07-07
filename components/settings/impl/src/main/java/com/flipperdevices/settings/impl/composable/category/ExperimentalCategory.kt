package com.flipperdevices.settings.impl.composable.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.CategoryElement
import com.flipperdevices.settings.impl.composable.elements.ClickableElement
import com.flipperdevices.settings.impl.composable.elements.GrayDivider
import com.flipperdevices.settings.impl.composable.elements.SwitchableElement
import com.flipperdevices.settings.impl.viewmodels.ExperimentalViewModel
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ExperimentalCategory(
    settings: Settings,
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
    experimentalViewModel: ExperimentalViewModel = tangleViewModel()
) {
    CardCategory(modifier = modifier) {
        CategoryElement(
            titleId = R.string.experimental_options,
            descriptionId = R.string.experimental_options_desc,
            state = settings.enabledExperimentalFunctions,
            onSwitchState = settingsViewModel::onSwitchExperimental
        )
        if (settings.enabledExperimentalFunctions) {
            ClickableElement(
                titleId = R.string.experimental_file_manager,
                descriptionId = R.string.experimental_file_manager_desc,
                onClick = { experimentalViewModel.onOpenFileManager(navController) }
            )
            GrayDivider()
            ClickableElement(
                titleId = R.string.experimental_screen_streaming,
                descriptionId = R.string.experimental_screen_streaming_desc,
                onClick = {
                    experimentalViewModel.onOpenScreenStreaming(navController)
                }
            )
            GrayDivider()
            SwitchableElement(
                titleId = R.string.experimental_application_catalog,
                state = settings.applicationCatalog,
                onSwitchState = experimentalViewModel::onSwitchApplicationCatalog
            )
        }
    }
}
