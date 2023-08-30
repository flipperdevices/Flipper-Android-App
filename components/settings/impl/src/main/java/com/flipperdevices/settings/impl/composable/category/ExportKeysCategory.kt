package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.SimpleElement
import com.flipperdevices.settings.impl.model.ExportState
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ExportKeysCategory(
    exportState: ExportState,
    onExport: () -> Unit
) {
    CardCategory {
        ExportKeysElement(
            exportState = exportState,
            onExport = onExport,
        )
    }
}

@Composable
private fun ExportKeysElement(
    exportState: ExportState,
    onExport: () -> Unit
) {
    Row(
        modifier = Modifier.clickableRipple(onClick = onExport),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleElement(
            Modifier.weight(weight = 1f),
            titleId = R.string.export_keys,
            descriptionId = null
        )
        when (exportState) {
            ExportState.NOT_STARTED -> Icon(
                modifier = Modifier
                    .size(size = 42.dp)
                    .padding(16.dp),
                painter = painterResource(DesignSystem.drawable.ic_navigate),
                tint = LocalPallet.current.iconTint30,
                contentDescription = null
            )
            ExportState.IN_PROGRESS -> CircularProgressIndicator(
                modifier = Modifier
                    .size(size = 42.dp)
                    .padding(16.dp)
            )
        }
    }
}
