package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.composable.LocalRouter
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.CategoryElement
import com.flipperdevices.settings.impl.composable.elements.ClickableElement
import com.flipperdevices.settings.impl.composable.elements.GrayDivider
import com.flipperdevices.settings.impl.model.NavGraphRoute
import com.flipperdevices.settings.impl.viewmodels.ExperimentalViewModel
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel

@Composable
fun ExperimentalCategory(
    settings: Settings,
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    experimentalViewModel: ExperimentalViewModel = viewModel()
) {
    val router = LocalRouter.current

    CardCategory {
        Column {
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
                    onClick = { experimentalViewModel.onOpenFileManager(router) }
                )
                GrayDivider()
                ClickableElement(
                    titleId = R.string.experimental_screen_streaming,
                    descriptionId = R.string.experimental_screen_streaming_desc,
                    onClick = {
                        navController.navigate(NavGraphRoute.ScreenStreaming.name) {
                            popUpTo(NavGraphRoute.Settings.name)
                        }
                    }
                )
            }
        }
    }
}
