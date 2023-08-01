package com.flipperdevices.settings.impl.composable.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.ClickableElement
import com.flipperdevices.settings.impl.composable.elements.GrayDivider
import com.flipperdevices.settings.impl.composable.elements.UrlElement
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel

@Composable
fun OtherSettingsCategory(
    settingsViewModel: SettingsViewModel,
    navController: NavController,
) {
    CardCategory {
        UrlElement(
            iconId = R.drawable.ic_forum,
            titleId = R.string.other_forum_open,
            url = stringResource(R.string.other_forum_url)
        )
        GrayDivider()
        UrlElement(
            iconId = R.drawable.ic_github,
            titleId = R.string.other_github_open,
            url = stringResource(R.string.other_github_url)
        )
        GrayDivider()
        val s2rInitialized by settingsViewModel.getShake2ReportInitializationState()
            .collectAsState()
        if (s2rInitialized) {
            ClickableElement(
                iconId = R.drawable.ic_bug,
                titleId = R.string.other_shake2report_open,
                onClick = { settingsViewModel.onReportBug(navController) }
            )
        }
    }
}
