package com.flipperdevices.faphub.fapscreen.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.errors.ComposableThrowableError
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.appcard.composable.components.AppCardScreenshots
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.fapscreen.impl.R
import com.flipperdevices.faphub.fapscreen.impl.composable.description.ComposableFapDescription
import com.flipperdevices.faphub.fapscreen.impl.composable.header.ComposableDeleteConfirmDialog
import com.flipperdevices.faphub.fapscreen.impl.composable.header.ComposableFapHeader
import com.flipperdevices.faphub.fapscreen.impl.model.FapDetailedControlState
import com.flipperdevices.faphub.fapscreen.impl.model.FapScreenLoadingState
import com.flipperdevices.faphub.fapscreen.impl.viewmodel.FapScreenViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableFapScreen(
    onBack: () -> Unit,
    installationButton: @Composable (FapItem?, Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = tangleViewModel<FapScreenViewModel>()
    val loadingState by viewModel.getLoadingState().collectAsState()
    val controlState by viewModel.getControlState().collectAsState()
    loadingState.let { loadingStateLocal ->
        when (loadingStateLocal) {
            is FapScreenLoadingState.Error -> ComposableThrowableError(
                throwable = loadingStateLocal.throwable,
                onRetry = viewModel::onRefresh,
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp)
            )

            is FapScreenLoadingState.Loaded -> ComposableFapScreenInternal(
                fapItem = loadingStateLocal.fapItem,
                onBack = onBack,
                installationButton = installationButton,
                modifier = modifier,
                controlState = controlState,
                onDelete = viewModel::onDelete
            )

            FapScreenLoadingState.Loading -> ComposableFapScreenInternal(
                fapItem = null,
                onBack = onBack,
                installationButton = installationButton,
                modifier = modifier,
                controlState = controlState,
                onDelete = viewModel::onDelete
            )
        }
    }
}

@Composable
private fun ComposableFapScreenInternal(
    fapItem: FapItem?,
    onBack: () -> Unit,
    controlState: FapDetailedControlState,
    onDelete: () -> Unit,
    installationButton: @Composable (FapItem?, Modifier) -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier.verticalScroll(rememberScrollState())) {
    ComposableFapScreenBar(fapItem?.name, onBack)
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog && fapItem != null) {
        ComposableDeleteConfirmDialog(
            fapItem = fapItem,
            onConfirmDelete = onDelete,
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
    ComposableFapHeader(
        modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp),
        fapItem = fapItem,
        installationButton = installationButton,
        controlState = controlState,
        onDelete = {
            showDeleteDialog = true
        }
    )
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        thickness = 1.dp,
        color = LocalPallet.current.fapHubDividerColor
    )
    AppCardScreenshots(
        screenshots = fapItem?.screenshots,
        modifier = Modifier.padding(top = 18.dp, start = 14.dp),
        screenshotModifier = Modifier
            .padding(end = 8.dp)
            .size(width = 189.dp, height = 94.dp),
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
) {
    OrangeAppBar(
        title = fapName ?: stringResource(R.string.fapscreen_title_default),
        onBack = onBack
    )
}
