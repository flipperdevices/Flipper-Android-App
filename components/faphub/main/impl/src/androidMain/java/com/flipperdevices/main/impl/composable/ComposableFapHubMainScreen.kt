package com.flipperdevices.main.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.main.impl.composable.switch.ComposableFapHubNewSwitch
import com.flipperdevices.main.impl.model.FapHubTabEnum

@Composable
fun ComposableFapHubMainScreen(
    onBack: () -> Unit,
    catalogTabComposable: @Composable () -> Unit,
    installedTabComposable: @Composable () -> Unit,
    onOpenSearch: () -> Unit,
    installedNotificationCount: Int,
    onSelect: (FapHubTabEnum) -> Unit,
    selectedTab: FapHubTabEnum,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ComposableFapHubNewSwitch(
            fapHubTabEnum = selectedTab,
            onSelect = onSelect,
            installedNotificationCount = installedNotificationCount,
            onBack = onBack,
            onEndClick = onOpenSearch
        )
        Spacer(
            modifier = Modifier
                .height(18.dp)
                .fillMaxWidth()
        )

        when (selectedTab) {
            FapHubTabEnum.APPS -> catalogTabComposable()
            FapHubTabEnum.INSTALLED -> installedTabComposable()
        }
    }
}
