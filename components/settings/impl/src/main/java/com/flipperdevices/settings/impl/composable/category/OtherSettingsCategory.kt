package com.flipperdevices.settings.impl.composable.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.GrayDivider
import com.flipperdevices.settings.impl.composable.elements.UrlElement
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel

@Composable
fun OtherSettingsCategory(
    settingsViewModel: SettingsViewModel, // used in ComposableSettings
    navController: NavController,         // used in ComposableSettings
) {
    CardCategory {
        UrlElement(
            iconId = R.drawable.ic_telegram,
            titleId = R.string.other_telegram_open,
            url = stringResource(R.string.other_telegram_url)
        )
        GrayDivider()
        UrlElement(
            iconId = R.drawable.ic_github,
            titleId = R.string.other_github_open,
            url = stringResource(R.string.other_github_url)
        )
        GrayDivider()
    }
}
