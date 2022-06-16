package com.flipperdevices.updater.screen.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.info.shared.R as SharedInfoResources
import com.flipperdevices.updater.screen.R
import com.flipperdevices.updater.screen.model.FailedReason
import com.flipperdevices.updater.screen.model.UpdaterScreenState

@Composable
fun ComposableUpdaterScreen(
    updaterScreenState: UpdaterScreenState,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    Column {
        Column(
            Modifier.weight(weight = 1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (updaterScreenState is UpdaterScreenState.Failed) {
                UpdaterScreenHeader(
                    R.string.update_screen_title_failed,
                    R.drawable.pic_flipper_update_failed,
                    bottomPadding = 38.dp
                )
            } else UpdaterScreenHeader(
                R.string.update_screen_title,
                SharedInfoResources.drawable.ic_white_flipper,
                bottomPadding = 64.dp
            )
            ComposableUpdateContent(updaterScreenState, onRetry)
        }
        CancelButton(updaterScreenState, onCancel)
    }
}

@Composable
private fun UpdaterScreenHeader(
    @StringRes titleId: Int,
    @DrawableRes imageId: Int,
    bottomPadding: Dp
) {
    Text(
        modifier = Modifier.padding(top = 48.dp, start = 14.dp, end = 14.dp),
        text = stringResource(titleId),
        fontSize = 18.sp,
        fontWeight = FontWeight.W700,
        color = colorResource(DesignSystem.color.black_100),
        textAlign = TextAlign.Center
    )

    Image(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 22.dp, start = 14.dp, end = 14.dp, bottom = bottomPadding),
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (updaterScreenState == UpdaterScreenState.CancelingUpdate) {
            CircularProgressIndicator(
                color = colorResource(DesignSystem.color.accent_secondary)
            )
            return@Box
        }
        Text(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onCancel
            ),
            text = stringResource(R.string.update_screen_cancel),
            textAlign = TextAlign.Center,
            color = colorResource(DesignSystem.color.accent_secondary),
            fontSize = 16.sp,
            fontWeight = FontWeight.W500
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenPreview() {
    ComposableUpdaterScreen(
        UpdaterScreenState.Failed(FailedReason.DOWNLOAD_FROM_NETWORK),
        onCancel = {},
        onRetry = {}
    )
}
