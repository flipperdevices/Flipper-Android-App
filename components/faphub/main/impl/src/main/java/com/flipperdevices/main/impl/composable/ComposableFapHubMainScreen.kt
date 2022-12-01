package com.flipperdevices.main.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.ktx.OrangeAppBarWithIcon
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.main.impl.R
import com.flipperdevices.main.impl.composable.switch.ComposableFapHubSwitch
import com.flipperdevices.main.impl.model.FapHubTabEnum
import com.flipperdevices.main.impl.viewmodel.InstalledNotificationViewModel

@Composable
fun ComposableFapHubMainScreen(
    onBack: () -> Unit,
    catalogTabComposable: @Composable () -> Unit,
    onOpenSearch: () -> Unit
) {
    val installedNotificationViewModel = viewModel<InstalledNotificationViewModel>()
    val installedNotificationCount by
    installedNotificationViewModel.getNotificationCountStateFlow().collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OrangeAppBarWithIcon(
            titleId = R.string.faphub_main_title,
            onBack = onBack,
            endIconId = DesignSystem.drawable.ic_search,
            onEndClick = onOpenSearch
        )

        var selectedTab by remember { mutableStateOf(FapHubTabEnum.APPS) }
        ComposableFapHubSwitch(
            modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
            fapHubTabEnum = selectedTab,
            onSelect = {
                selectedTab = it
            },
            installedNotificationCount = installedNotificationCount
        )

        catalogTabComposable()
    }
}
