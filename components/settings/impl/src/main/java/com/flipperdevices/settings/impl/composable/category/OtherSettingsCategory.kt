package com.flipperdevices.settings.impl.composable.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.ClickableElement
import com.flipperdevices.settings.impl.composable.elements.GrayDivider
import com.flipperdevices.settings.impl.composable.elements.UrlElement

@Composable
fun OtherSettingsCategory(
    s2rInitialized: Boolean,
    onReportBug: () -> Unit,
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
        if (s2rInitialized) {
            ClickableElement(
                iconId = R.drawable.ic_bug,
                titleId = R.string.other_shake2report_open,
                onClick = onReportBug
            )
        }
    }
}
