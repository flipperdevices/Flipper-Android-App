package com.flipperdevices.main.impl.composable

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.OrangeAppBarWithIcon
import com.flipperdevices.main.impl.R
import com.flipperdevices.main.impl.composable.switch.ComposableFapHubSwitch
import com.flipperdevices.main.impl.model.FapHubTabEnum

@Composable
fun ComposableFapHubMainScreen(
    onBack: () -> Unit,
    catalogTabComposable: @Composable () -> Unit,
    onOpenSearch: () -> Unit
) {
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
            }
        )

        catalogTabComposable()
    }
}
