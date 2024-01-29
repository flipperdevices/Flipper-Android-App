package com.flipperdevices.faphub.installedtab.impl.composable.online

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.appcard.composable.components.ComposableAppCategory
import com.flipperdevices.faphub.appcard.composable.components.ComposableAppIcon
import com.flipperdevices.faphub.dao.api.model.FapItemShort

@Composable
fun ComposableOnlineFapApp(
    fapItem: FapItemShort?,
    installationButton: @Composable (Modifier) -> Unit,
    uninstallButton: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableAppIcon(
            url = fapItem?.picUrl,
            description = fapItem?.name,
            modifier = Modifier.size(42.dp)
        )
        Column(
            Modifier
                .padding(horizontal = 8.dp)
                .weight(weight = 1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                modifier = if (fapItem == null) Modifier.placeholderConnecting() else Modifier,
                text = fapItem?.name ?: "Unknown",
                style = LocalTypography.current.bodyM14,
                color = LocalPallet.current.text100
            )

            ComposableAppCategory(
                category = fapItem?.category
            )
        }

        installationButton(
            Modifier
                .padding(vertical = 4.dp)
        )

        uninstallButton(
            Modifier
                .padding(start = 12.dp)
                .size(34.dp)
        )
    }
}
