package com.flipperdevices.faphub.fapscreen.impl.composable

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.OrangeAppBarWithIcon
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.appcard.composable.components.AppCardScreenshots
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.fapscreen.impl.R
import com.flipperdevices.faphub.fapscreen.impl.composable.description.ComposableFapDescription
import com.flipperdevices.faphub.fapscreen.impl.composable.header.ComposableFapHeader
import com.flipperdevices.faphub.fapscreen.impl.model.FapScreenLoadingState
import com.flipperdevices.faphub.fapscreen.impl.viewmodel.FapScreenViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableFapScreen(
    onBack: () -> Unit,
    onSearch: () -> Unit,
    installationButton: @Composable (FapItem?, Modifier, TextUnit) -> Unit
) {
    val viewModel = tangleViewModel<FapScreenViewModel>()
    val loadingState by viewModel.getLoadingState().collectAsState()
    val fapItem = (loadingState as? FapScreenLoadingState.Loaded)?.fapItem

    ComposableFapScreenInternal(fapItem, onBack, onSearch, installationButton)
}

@Composable
private fun ComposableFapScreenInternal(
    fapItem: FapItem?,
    onBack: () -> Unit,
    onSearch: () -> Unit,
    installationButton: @Composable (FapItem?, Modifier, TextUnit) -> Unit
) = Column(Modifier.verticalScroll(rememberScrollState())) {
    ComposableFapScreenBar(fapItem?.name, onBack, onSearch)
    ComposableFapHeader(
        modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp),
        fapItem = fapItem,
        installationButton = installationButton
    )
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        thickness = 1.dp,
        color = LocalPallet.current.fapHubDividerColor
    )
    AppCardScreenshots(
        modifier = Modifier.padding(top = 18.dp, start = 14.dp),
        screenshotModifier = Modifier
            .padding(end = 8.dp)
            .size(width = 189.dp, height = 94.dp),
        screenshots = fapItem?.screenshots
    )
    ComposableFapDescription(
        modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 36.dp),
        fapItem = fapItem
    )
}


@Composable
private fun ComposableFapScreenBar(
    fapName: String?,
    onBack: () -> Unit,
    onSearch: () -> Unit
) {
    OrangeAppBarWithIcon(
        title = fapName ?: stringResource(R.string.fapscreen_title_default),
        endIconId = DesignSystem.drawable.ic_search,
        onBack = onBack,
        onEndClick = onSearch
    )
}