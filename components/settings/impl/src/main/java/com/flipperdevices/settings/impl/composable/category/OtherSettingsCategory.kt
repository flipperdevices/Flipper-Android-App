package com.flipperdevices.settings.impl.composable.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.ClickableElement
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel

@Composable
fun OtherSettingsCategory(
    settingsViewModel: SettingsViewModel,
    navController: NavController,
) {
    val s2rInitialized by settingsViewModel.getShake2ReportInitializationState().collectAsState()
    if (!s2rInitialized) {
        return
    }
    CardCategory {
        ClickableElement(
            titleId = R.string.debug_shake2report_open,
            onClick = { settingsViewModel.onReportBug(navController) }
        )
    }
}
