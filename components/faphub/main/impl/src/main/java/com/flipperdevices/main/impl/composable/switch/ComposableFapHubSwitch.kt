package com.flipperdevices.main.impl.composable.switch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.tabswitch.ComposableTabSwitch
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.main.impl.model.FapHubTabEnum
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableFapHubNewSwitch(
    fapHubTabEnum: FapHubTabEnum,
    onSelect: (FapHubTabEnum) -> Unit,
    installedNotificationCount: Int,
    onEndClick: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalPallet.current.accent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            Image(
                modifier = Modifier
                    .padding(top = 11.dp, bottom = 11.dp, start = 16.dp, end = 2.dp)
                    .size(20.dp)
                    .clickableRipple(onClick = onBack),
                painter = painterResource(DesignSystem.drawable.ic_back),
                contentDescription = null
            )
        }
        ComposableTabSwitch(
            typeTab = FapHubTabEnum::class.java,
            currentTab = fapHubTabEnum,
            modifier = Modifier.weight(1f)
        ) {
            ComposableFapHubTab(
                hubTabEnum = it,
                onSelectFapHubTabEnum = onSelect,
                if (it == FapHubTabEnum.INSTALLED) {
                    installedNotificationCount
                } else {
                    0
                }
            )
        }
        Icon(
            modifier = Modifier
                .padding(end = 14.dp)
                .size(24.dp)
                .clickableRipple(onClick = onEndClick),
            painter = painterResource(DesignSystem.drawable.ic_search),
            contentDescription = null,
            tint = LocalPallet.current.onAppBar
        )
    }
}
