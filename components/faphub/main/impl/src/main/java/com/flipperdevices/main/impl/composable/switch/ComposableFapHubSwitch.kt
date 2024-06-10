package com.flipperdevices.main.impl.composable.switch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import java.lang.Integer.min
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val MAX_INSTALLED_NUMBER = 99

@Composable
fun ComposableFapHubNewSwitch(
    fapHubTabEnum: FapHubTabEnum,
    onSelect: (FapHubTabEnum) -> Unit,
    installedNotificationCount: Int,
    onEndClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalPallet.current.accent)
            .padding(vertical = 4.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableTabSwitch(
            typeTab = FapHubTabEnum::class.java,
            currentTab = fapHubTabEnum,
            modifier = Modifier
                .weight(1f)
                .padding(start = 52.dp) // 14*2+24
        ) {
            ComposableFapHubTab(
                hubTabEnum = it,
                onSelectFapHubTabEnum = onSelect,
                if (it == FapHubTabEnum.INSTALLED) {
                    min(MAX_INSTALLED_NUMBER, installedNotificationCount)
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
