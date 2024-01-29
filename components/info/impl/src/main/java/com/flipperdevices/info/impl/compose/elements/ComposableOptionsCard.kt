package com.flipperdevices.info.impl.compose.elements

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.shared.ButtonElementRow
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableOptionsCard(
    modifier: Modifier = Modifier,
    onOpenOptions: () -> Unit
) {
    InfoElementCard(modifier) {
        ButtonElementRow(
            titleId = R.string.info_device_options,
            iconId = R.drawable.ic_options,
            color = LocalPallet.current.text80,
            onClick = onOpenOptions,
            actionIconId = DesignSystem.drawable.ic_navigate
        )
    }
}
