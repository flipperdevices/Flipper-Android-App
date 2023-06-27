package com.flipperdevices.main.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.SetUpStatusBarColor
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.main.impl.composable.switch.ComposableFapHubNewSwitch
import com.flipperdevices.main.impl.model.FapHubTabEnum
import com.flipperdevices.main.impl.viewmodel.MainViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableFapHubMainScreen(
    onBack: () -> Unit,
    catalogTabComposable: @Composable () -> Unit,
    installedTabComposable: @Composable () -> Unit,
    onOpenSearch: () -> Unit,
    installedNotificationCount: Int,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = tangleViewModel()
) {
    val selectedTab by mainViewModel.getTabFlow().collectAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SetUpStatusBarColor(LocalPallet.current.accent, darkIcon = true)
        ComposableFapHubNewSwitch(
            fapHubTabEnum = selectedTab,
            onSelect = mainViewModel::onSelectTab,
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
