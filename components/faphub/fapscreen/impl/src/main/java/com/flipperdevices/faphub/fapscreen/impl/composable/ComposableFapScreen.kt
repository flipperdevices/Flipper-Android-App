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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.elements.SwipeRefresh
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.appcard.composable.components.AppCardScreenshots
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.errors.api.throwable.toFapHubError
import com.flipperdevices.faphub.fapscreen.impl.R
import com.flipperdevices.faphub.fapscreen.impl.composable.description.ComposableFapDescription
import com.flipperdevices.faphub.fapscreen.impl.composable.header.ComposableFapHeader
import com.flipperdevices.faphub.fapscreen.impl.model.FapDetailedControlState
import com.flipperdevices.faphub.fapscreen.impl.model.FapScreenLoadingState
import com.flipperdevices.faphub.screenshotspreview.api.model.ScreenshotsPreviewParam
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig

@Composable
fun ComposableFapScreen(
    onBack: () -> Unit,
    onOpenDeviceTab: () -> Unit,
    onRefresh: () -> Unit,
    onPressHide: (FapScreenLoadingState.Loaded) -> Unit,
    onOpenReport: (FapScreenLoadingState.Loaded) -> Unit,
    loadingState: FapScreenLoadingState,
    controlState: FapDetailedControlState,
    uninstallButton: @Composable (Modifier, FapItem) -> Unit,
    installationButton: @Composable (FapItem?, Modifier) -> Unit,
    errorsRenderer: FapHubComposableErrorsRenderer,
    modifier: Modifier = Modifier
) {
    when (loadingState) {
        is FapScreenLoadingState.Error -> errorsRenderer.ComposableThrowableError(
            throwable = loadingState.throwable.toFapHubError(),
            onRetry = onRefresh,
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            fapErrorSize = FapErrorSize.FULLSCREEN
        )

        is FapScreenLoadingState.Loaded -> ComposableFapScreenInternal(
            fapItem = loadingState.fapItem,
            onBack = onBack,
            installationButton = installationButton,
            modifier = modifier,
            controlState = controlState,
            uninstallButton = { uninstallButton(it, loadingState.fapItem) },
            onOpenDeviceTab = onOpenDeviceTab,
            shareUrl = loadingState.shareUrl,
            onReportApp = { onOpenReport(loadingState) },
            onRefresh = onRefresh,
            isHidden = loadingState.isHidden,
            onHideApp = { onPressHide(loadingState) }
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
            onRefresh = onRefresh,
            isHidden = true,
            onHideApp = {}
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
    onHideApp: () -> Unit,
    isHidden: Boolean,
    installationButton: @Composable (FapItem?, Modifier) -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier) {
    val rootNavigation = LocalRootNavigation.current
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
                onScreenshotClick = onScreenshotClick@{ index ->
                    val requireFapItem = fapItem ?: return@onScreenshotClick
                    val param = ScreenshotsPreviewParam(
                        title = requireFapItem.name,
                        screenshotsUrls = requireFapItem.screenshots,
                        selected = index
                    )
                    rootNavigation.push(RootScreenConfig.ScreenshotPreview(param))
                },
                screenshotModifier = Modifier
                    .padding(end = 8.dp)
                    .size(width = 189.dp, height = 94.dp),
            )
            ComposableFapDescription(
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 36.dp),
                fapItem = fapItem,
                onReportApp = onReportApp,
                onHideApp = onHideApp,
                isHidden = isHidden
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
