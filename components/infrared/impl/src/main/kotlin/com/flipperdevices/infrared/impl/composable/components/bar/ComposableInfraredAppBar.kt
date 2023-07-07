package com.flipperdevices.infrared.impl.composable.components.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.SetUpStatusBarColor
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.tabswitch.ComposableTabSwitch
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.infrared.impl.model.InfraredTab
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
internal fun ComposableInfraredAppBar(
    onBack: () -> Unit,
    currentTab: InfraredTab,
    onChangeTab: (InfraredTab) -> Unit,
    onFavorite: () -> Unit,
    onEdit: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    isFavorite: Boolean,
) {
    SetUpStatusBarColor(color = LocalPallet.current.accent, darkIcon = true)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalPallet.current.accent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(top = 11.dp, bottom = 11.dp, start = 16.dp, end = 2.dp)
                .size(20.dp)
                .clickableRipple(onClick = onBack),
            painter = painterResource(DesignSystem.drawable.ic_back),
            contentDescription = null
        )
        ComposableTabSwitch(
            typeTab = InfraredTab::class.java,
            currentTab = currentTab,
            modifier = Modifier.weight(1f)
        ) { tab ->
            ComposableInfraredTab(
                tab = tab,
                onSelect = onChangeTab
            )
        }
        ComposableInfraredDropDown(
            modifier = Modifier.padding(end = 14.dp),
            onFavorite = onFavorite,
            onEdit = onEdit,
            onRename = onRename,
            onDelete = onDelete,
            onShare = onShare,
            isFavorite = isFavorite,
        )
    }
}

@Preview
@Composable
private fun InfraredAppBar() {
    FlipperThemeInternal {
        ComposableInfraredAppBar(
            onBack = { }, currentTab = InfraredTab.INFO,
            onChangeTab = {},
            onFavorite = {},
            onEdit = {},
            onRename = {},
            onDelete = {},
            onShare = {}, isFavorite = false
        )
    }
}
