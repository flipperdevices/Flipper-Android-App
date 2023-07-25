package com.flipperdevices.infrared.impl.composable.components.bar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.image.painterResourceByKey
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.infrared.impl.R
import com.flipperdevices.infrared.impl.model.InfraredTab
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
internal fun ComposableInfraredTab(
    tab: InfraredTab,
    onSelect: (InfraredTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val titleId = when (tab) {
        InfraredTab.REMOTE -> R.string.infrared_tab_remote
        InfraredTab.INFO -> R.string.infrared_tab_info
    }

    val iconId = when (tab) {
        InfraredTab.REMOTE -> DesignSystem.drawable.ic_fileformat_ir
        InfraredTab.INFO -> DesignSystem.drawable.ic_warning
    }

    Row(
        modifier = modifier.clickableRipple { onSelect(tab) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 8.dp)
                .size(18.dp),
            painter = painterResourceByKey(iconId),
            contentDescription = stringResource(titleId),
            tint = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
        )
        Text(
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 8.dp,
                end = 12.dp
            ),
            text = stringResource(titleId),
            color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
            style = LocalTypography.current.subtitleM12
        )
    }
}
