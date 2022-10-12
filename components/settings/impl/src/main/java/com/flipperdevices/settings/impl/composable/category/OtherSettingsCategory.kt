package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.ClickableElement
import com.flipperdevices.settings.impl.composable.elements.GrayDivider
import com.flipperdevices.settings.impl.composable.elements.SimpleElement
import com.flipperdevices.settings.impl.model.ExportState.IN_PROGRESS
import com.flipperdevices.settings.impl.model.ExportState.NOT_STARTED
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel

@Composable
fun OtherSettingsCategory(
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    CardCategory {
        Column {
            ClickableElement(
                titleId = R.string.debug_shake2report_open,
                onClick = { settingsViewModel.onReportBug(context) }
            )
            GrayDivider()
            ExportKeysElement(settingsViewModel)
        }
    }
}

@Composable
fun ExportKeysElement(
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val exportState by settingsViewModel.getExportState().collectAsState()
    Row(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(),
            onClick = { settingsViewModel.onMakeExport(context) }
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleElement(
            Modifier.weight(weight = 1f),
            titleId = R.string.export_keys,
            descriptionId = null
        )
        when (exportState) {
            NOT_STARTED -> Icon(
                modifier = Modifier
                    .size(size = 42.dp)
                    .padding(16.dp),
                painter = painterResource(DesignSystem.drawable.ic_navigate_icon),
                tint = LocalPallet.current.iconTint30,
                contentDescription = null
            )
            IN_PROGRESS -> CircularProgressIndicator(
                modifier = Modifier
                    .size(size = 42.dp)
                    .padding(16.dp)
            )
        }
    }
}
