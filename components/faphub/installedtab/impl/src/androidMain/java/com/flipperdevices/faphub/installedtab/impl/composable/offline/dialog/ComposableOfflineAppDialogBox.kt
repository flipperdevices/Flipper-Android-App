package com.flipperdevices.faphub.installedtab.impl.composable.offline.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.installedtab.impl.composable.offline.ComposableOfflineAppCategory
import com.flipperdevices.faphub.installedtab.impl.composable.offline.ComposableOfflineAppIcon
import com.flipperdevices.faphub.installedtab.impl.model.OfflineFapApp

@Composable
fun ComposableOfflineAppDialogBox(
    offlineFapApp: OfflineFapApp,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier
        .clip(RoundedCornerShape(12.dp))
        .background(LocalPallet.current.fapHubDeleteDialogBackground),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
) {
    ComposableOfflineAppIcon(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .size(42.dp),
        iconBase64 = offlineFapApp.iconBase64
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = offlineFapApp.name,
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.text100
        )
        ComposableOfflineAppCategory(category = offlineFapApp.category)
    }
}
