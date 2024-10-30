package com.flipperdevices.settings.impl.composable.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.components.CategoryElement
import com.flipperdevices.settings.impl.composable.components.ClickableElement

@Composable
fun ExperimentalCategory(
    settings: Settings,
    onOpenFM: () -> Unit,
    onSwitchExperimental: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    CardCategory(modifier = modifier) {
        CategoryElement(
            titleId = R.string.experimental_options,
            descriptionId = R.string.experimental_options_desc,
            state = settings.enabled_experimental_functions,
            onSwitchState = onSwitchExperimental
        )
        if (settings.enabled_experimental_functions) {
            ClickableElement(
                titleId = R.string.experimental_file_manager,
                descriptionId = R.string.experimental_file_manager_desc,
                onClick = onOpenFM
            )
        }
    }
}
