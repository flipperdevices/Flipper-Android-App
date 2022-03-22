package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.SwitchableElement

@Composable
fun RowScope.GeneralCategory(
    settings: Settings,
    onSwitchExperimental: (Boolean) -> Unit,
    onSwitchDebug: (Boolean) -> Unit
) {
    SwitchableElement(
        titleId = R.string.general_debug_title,
        descriptionId = R.string.general_debug_description,
        state = settings.enabledDebugSettings,
        onSwitchState = onSwitchDebug
    )
    SwitchableElement(
        titleId = R.string.general_experimental_title,
        descriptionId = R.string.general_experimental_description,
        state = settings.enabledExperimentalFunctions,
        onSwitchState = onSwitchExperimental
    )
}
