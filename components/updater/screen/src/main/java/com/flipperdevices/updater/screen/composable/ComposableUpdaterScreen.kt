package com.flipperdevices.updater.screen.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.ktx.SetUpNavigationBarColor
import com.flipperdevices.core.ui.ktx.SetUpStatusBarColor
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.updater.screen.R
import com.flipperdevices.updater.screen.model.FailedReason
import com.flipperdevices.updater.screen.model.UpdaterScreenState
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableUpdaterScreen(
    updaterScreenState: UpdaterScreenState,
    flipperColor: HardwareColor,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    SetUpStatusBarColor(LocalPallet.current.background)
    Column(
        modifier
            .background(LocalPallet.current.background)
    ) {
        Column(
            Modifier
                .weight(weight = 1f)
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UpdaterScreenHeader(
                updaterScreenState,
                flipperColor = flipperColor
            )
            ComposableUpdateContent(updaterScreenState, onRetry)
            val changelog = updaterScreenState.updateRequest?.changelog
            if (changelog != null) {
                ComposableChangelog(changelog)
            }
        }
        CancelButton(updaterScreenState, onCancel)
    }
}

@Composable
private fun ColumnScope.UpdaterScreenHeader(
    updaterScreenState: UpdaterScreenState,
    flipperColor: HardwareColor
) {
    val titleId = if (updaterScreenState is UpdaterScreenState.Failed) {
        R.string.update_screen_title_failed
    } else {
        R.string.update_screen_title
    }
    Text(
        modifier = Modifier.padding(vertical = 18.dp),
        text = stringResource(titleId),
        style = LocalTypography.current.titleB18,
        textAlign = TextAlign.Center
    )

    val imageId = when (flipperColor) {
        HardwareColor.UNRECOGNIZED,
        HardwareColor.WHITE -> if (updaterScreenState is UpdaterScreenState.Failed) {
            when (updaterScreenState.failedReason) {
                FailedReason.FAILED_SUB_GHZ_PROVISIONING,
                FailedReason.FAILED_INT_STORAGE ->
                    DesignSystem.drawable.pic_flipper_update_int_failed
                else -> DesignSystem.drawable.pic_flipper_update_failed
            }
        } else {
            DesignSystem.drawable.pic_flipper_update
        }
        HardwareColor.BLACK -> if (updaterScreenState is UpdaterScreenState.Failed) {
            when (updaterScreenState.failedReason) {
                FailedReason.FAILED_SUB_GHZ_PROVISIONING,
                FailedReason.FAILED_INT_STORAGE ->
                    DesignSystem.drawable.pic_black_flipper_update_int_failed
                else -> DesignSystem.drawable.pic_black_flipper_update_failed
            }
        } else {
            DesignSystem.drawable.pic_black_flipper_update
        }
    }

    Image(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        painter = painterResource(imageId),
        contentDescription = stringResource(titleId),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun CancelButton(
    updaterScreenState: UpdaterScreenState,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (updaterScreenState == UpdaterScreenState.CancelingUpdate) {
            CircularProgressIndicator(
                modifier = Modifier.padding(vertical = 8.dp),
                color = LocalPallet.current.accentSecond
            )
            return
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickableRipple(onClick = onCancel)
                .padding(vertical = 8.dp),
            text = stringResource(R.string.update_screen_cancel),
            textAlign = TextAlign.Center,
            color = LocalPallet.current.accentSecond,
            style = LocalTypography.current.buttonM16
        )
    }
    SetUpNavigationBarColor(LocalPallet.current.background)
}
