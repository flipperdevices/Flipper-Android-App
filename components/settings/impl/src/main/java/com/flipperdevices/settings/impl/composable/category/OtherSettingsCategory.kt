package com.flipperdevices.settings.impl.composable.category

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.ClickableElement
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel

@Composable
fun OtherSettingsCategory(
    settingsViewModel: SettingsViewModel,
    navController: NavController
) {
    CardCategory {
        ClickableElement(
            titleId = R.string.debug_shake2report_open,
            onClick = { settingsViewModel.onReportBug(navController) }
        )
    }
}
