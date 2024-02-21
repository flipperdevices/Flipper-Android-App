package com.flipperdevices.faphub.fapscreen.impl.composable.header

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.dao.api.model.FapBuildState
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.fapscreen.impl.R

@Composable
fun ComposableFapBuildStatus(
    fapItem: FapItem,
    onOpenDeviceTab: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }

    ComposableFapBuildStatusCard(
        fapBuildState = fapItem.upToDateVersion.buildState,
        modifier = modifier,
        onClick = { showDialog = true }
    )

    if (showDialog) {
        ComposableFapBuildStatusDialog(
            fapItem = fapItem,
            onOpenDeviceTab = onOpenDeviceTab,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun ComposableFapBuildStatusCard(
    fapBuildState: FapBuildState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (fapBuildState) {
        FapBuildState.READY -> {}
        FapBuildState.READY_ON_RELEASE -> ComposableStatusCard(
            modifier = modifier,
            cardColor = LocalPallet.current.fapHubBuildStatusReadyBackground,
            textColor = LocalPallet.current.fapHubBuildStatusReadyText,
            cardIconId = R.drawable.ic_circle_check,
            cardTextId = R.string.fapscreen_building_state_ready_text,
            onClick = onClick
        )

        FapBuildState.BUILD_RUNNING -> ComposableStatusCard(
            modifier = modifier,
            cardColor = LocalPallet.current.fapHubBuildStatusRebuildingBackground,
            textColor = LocalPallet.current.fapHubBuildStatusRebuildingText,
            cardIconId = R.drawable.ic_triangle_warning,
            cardTextId = R.string.fapscreen_building_state_building_text,
            onClick = onClick
        )

        FapBuildState.UNSUPPORTED_APP -> ComposableStatusCard(
            modifier = modifier,
            cardColor = LocalPallet.current.fapHubBuildStatusFailedBackground,
            textColor = LocalPallet.current.fapHubBuildStatusFailedText,
            cardIconId = R.drawable.ic_triangle_warning,
            cardTextId = R.string.fapscreen_building_state_outdated_app_text,
            onClick = onClick
        )

        FapBuildState.FLIPPER_OUTDATED,
        FapBuildState.UNSUPPORTED_SDK -> ComposableStatusCard(
            modifier = modifier,
            cardColor = LocalPallet.current.fapHubBuildStatusFailedBackground,
            textColor = LocalPallet.current.fapHubBuildStatusFailedText,
            cardIconId = null,
            cardTextId = R.string.fapscreen_building_state_outdated_flipper_text,
            onClick = onClick
        )
    }
}

@Composable
private fun ComposableStatusCard(
    cardColor: Color,
    textColor: Color,
    @DrawableRes cardIconId: Int?,
    @StringRes cardTextId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier
        .clip(RoundedCornerShape(8.dp))
        .background(cardColor)
        .clickableRipple(onClick = onClick),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
) {
    Box(
        modifier = Modifier
            .padding(start = 12.dp, top = 10.dp, bottom = 10.dp)
            .size(12.dp)
    )
    Row(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (cardIconId != null) {
            Icon(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .size(16.dp),
                painter = painterResource(cardIconId),
                contentDescription = stringResource(cardTextId),
                tint = textColor
            )
        }

        Text(
            text = stringResource(cardTextId),
            color = textColor,
            style = LocalTypography.current.subtitleR12,
            textAlign = TextAlign.Center
        )
    }

    Icon(
        modifier = Modifier
            .padding(end = 12.dp, top = 10.dp, bottom = 10.dp)
            .size(12.dp),
        painter = painterResource(R.drawable.ic_info),
        contentDescription = null,
        tint = LocalPallet.current.fapHubBuildStatusInfo
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFapBuildStatusPreview() {
    FlipperThemeInternal {
        Column {
            FapBuildState.values().forEach { fapBuildState ->
                ComposableFapBuildStatusCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    fapBuildState = fapBuildState,
                    onClick = {}
                )
            }
        }
    }
}
