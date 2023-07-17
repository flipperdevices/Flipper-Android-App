package com.flipperdevices.faphub.fapscreen.impl.composable

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.elements.SwipeRefresh
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.appcard.composable.components.AppCardScreenshots
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.fapscreen.impl.R
import com.flipperdevices.faphub.fapscreen.impl.composable.description.ComposableFapDescription
import com.flipperdevices.faphub.fapscreen.impl.composable.header.ComposableFapHeader
import com.flipperdevices.faphub.fapscreen.impl.model.FapDetailedControlState
import com.flipperdevices.faphub.fapscreen.impl.model.FapScreenLoadingState
import com.flipperdevices.faphub.fapscreen.impl.viewmodel.FapScreenViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableFapScreen(
    navController: NavController,
    onBack: () -> Unit,
    onOpenDeviceTab: () -> Unit,
    uninstallButton: @Composable (Modifier, FapItem) -> Unit,
    installationButton: @Composable (FapItem?, Modifier) -> Unit,
    errorsRenderer: FapHubComposableErrorsRenderer,
    modifier: Modifier = Modifier
) {
    val viewModel = tangleViewModel<FapScreenViewModel>()
    val loadingState by viewModel.getLoadingState().collectAsState()
    val controlState by viewModel.getControlState().collectAsState()

    when (val loadingStateLocal = loadingState) {
        is FapScreenLoadingState.Error -> errorsRenderer.ComposableThrowableError(
            throwable = loadingStateLocal.throwable,
            onRetry = viewModel::onRefresh,
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            fapErrorSize = FapErrorSize.FULLSCREEN
        )

        is FapScreenLoadingState.Loaded -> ComposableFapScreenInternal(
            fapItem = loadingStateLocal.fapItem,
            onBack = onBack,
            installationButton = installationButton,
            modifier = modifier,
            controlState = controlState,
            uninstallButton = { uninstallButton(it, loadingStateLocal.fapItem) },
            onOpenDeviceTab = onOpenDeviceTab,
            shareUrl = loadingStateLocal.shareUrl,
            onReportApp = { viewModel.onOpenReportApp(navController) },
            onRefresh = viewModel::onRefresh
        )

        FapScreenLoadingState.Loading -> ComposableFapScreenInternal(
            fapItem = null,
            onBack = onBack,
            installationButton = installationButton,
            modifier = modifier,
            controlState = controlState,
            uninstallButton = {},
            onOpenDeviceTab = onOpenDeviceTab,
            shareUrl = null,
            onReportApp = {},
            onRefresh = viewModel::onRefresh
        )
    }
}

@Composable
private fun ComposableFapScreenInternal(
    fapItem: FapItem?,
    shareUrl: String?,
    onBack: () -> Unit,
    controlState: FapDetailedControlState,
    uninstallButton: @Composable (Modifier) -> Unit,
    onOpenDeviceTab: () -> Unit,
    onReportApp: () -> Unit,
    onRefresh: () -> Unit,
    installationButton: @Composable (FapItem?, Modifier) -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier) {
    ComposableFapScreenBar(fapName = fapItem?.name, url = shareUrl, onBack = onBack)
    SwipeRefresh(onRefresh = onRefresh) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            ComposableFapHeader(
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp),
                fapItem = fapItem,
                installationButton = installationButton,
                controlState = controlState,
                uninstallButton = uninstallButton,
                onOpenDeviceTab = onOpenDeviceTab
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
                fapItem = fapItem,
                onReportApp = onReportApp
            )
        }
    }
}

@Composable
private fun ComposableFapScreenBar(
    fapName: String?,
    url: String?,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val shareTitle = stringResource(R.string.fapscreen_install_share_desc)

    OrangeAppBar(
        title = fapName ?: stringResource(R.string.fapscreen_title_default),
        onBack = onBack,
        endBlock = { modifier ->
            if (url != null) {
                Icon(
                    modifier = modifier
                        .size(24.dp)
                        .clickableRipple {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, url)
                                type = "text/plain"
                            }
                            ContextCompat.startActivity(
                                context,
                                Intent.createChooser(intent, shareTitle),
                                null
                            )
                        },
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = shareTitle,
                    tint = LocalPallet.current.onAppBar
                )
            }
        }
    )
}
