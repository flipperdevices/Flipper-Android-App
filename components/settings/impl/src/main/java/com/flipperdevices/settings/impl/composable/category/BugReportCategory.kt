package com.flipperdevices.settings.impl.composable.category

import androidx.compose.runtime.Composable
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.ClickableElement

@Composable
fun BugReportCategory(
    onClick: () -> Unit
) {
    CardCategory {
        ClickableElement(
            titleId = R.string.debug_shake2report_open,
            onClick = onClick
        )
    }
}
