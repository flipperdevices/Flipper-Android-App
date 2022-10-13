package com.flipperdevices.settings.impl.composable.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.ClickableElement
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel

@Composable
fun OtherSettingsCategory(
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    CardCategory {
        ClickableElement(
            titleId = R.string.debug_shake2report_open,
            onClick = { settingsViewModel.onReportBug(context) }
        )
    }
}
