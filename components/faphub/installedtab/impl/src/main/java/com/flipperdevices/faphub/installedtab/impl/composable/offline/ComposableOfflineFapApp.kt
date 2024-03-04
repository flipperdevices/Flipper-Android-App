package com.flipperdevices.faphub.installedtab.impl.composable.offline

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.installedtab.impl.model.InstalledFapApp

@Composable
fun ComposableOfflineFapApp(
    offlineFapApp: InstalledFapApp.OfflineFapApp,
    uninstallButton: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableOfflineAppIcon(
            iconBase64 = offlineFapApp.iconBase64,
            modifier = Modifier.size(42.dp)
        )
        Column(
            Modifier
                .padding(horizontal = 8.dp)
                .weight(weight = 1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = offlineFapApp.name,
                style = LocalTypography.current.bodyM14,
                color = LocalPallet.current.text100
            )

            ComposableOfflineAppCategory(offlineFapApp.category)
        }

        ComposableOfflineAppButton(
            modifier = Modifier
                .padding(vertical = 4.dp)
        )

        uninstallButton(
            Modifier
                .padding(start = 12.dp)
                .size(34.dp)
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun PreviewComposableOfflineFapApp() {
    FlipperThemeInternal {
        ComposableOfflineFapApp(
            InstalledFapApp.OfflineFapApp(
                name = "Test App",
                iconBase64 = null,
                category = "Game",
                applicationUid = "test",
                applicationAlias = "test"
            ),
            uninstallButton = {}
        )
    }
}
