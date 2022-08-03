package com.flipperdevices.updater.screen.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.updater.screen.R
import com.flipperdevices.updater.screen.model.UpdaterScreenState

@Composable
fun ComposableUpdaterScreen(
    updaterScreenState: UpdaterScreenState,
    flipperColor: HardwareColor,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    Column {
        Column(
            Modifier.weight(weight = 1f).padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UpdaterScreenHeader(
                isFailed = updaterScreenState is UpdaterScreenState.Failed,
                flipperColor = flipperColor
            )
            ComposableUpdateContent(updaterScreenState, onRetry)
            val changelog = updaterScreenState.firmwareData?.changelog
            if (changelog != null) {
                ComposableChangelog(changelog)
            }
        }
        CancelButton(updaterScreenState, onCancel)
    }
}

@Composable
private fun UpdaterScreenHeader(
    isFailed: Boolean,
    flipperColor: HardwareColor
) {
    val titleId = if (isFailed) {
        R.string.update_screen_title_failed
    } else R.string.update_screen_title
    Text(
        modifier = Modifier.padding(vertical = 18.dp),
        text = stringResource(titleId),
        style = LocalTypography.current.titleB18,
        textAlign = TextAlign.Center
    )

    val imageId = when (flipperColor) {
        HardwareColor.UNRECOGNIZED,
        HardwareColor.WHITE -> if (isFailed) {
            DesignSystem.drawable.pic_flipper_update_failed
        } else DesignSystem.drawable.pic_flipper_update
        HardwareColor.BLACK -> if (isFailed) {
            DesignSystem.drawable.pic_black_flipper_update_failed
        } else DesignSystem.drawable.pic_black_flipper_update
    }

    Image(
        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
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
            return@Box
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = onCancel
                )
                .padding(vertical = 8.dp),
            text = stringResource(R.string.update_screen_cancel),
            textAlign = TextAlign.Center,
            color = LocalPallet.current.accentSecond,
            style = LocalTypography.current.buttonM16
        )
    }
}
