package com.flipperdevices.screenstreaming.impl.composable.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.screenstreaming.impl.R
import com.flipperdevices.screenstreaming.impl.model.FlipperLockState

@Composable
fun ComposableFlipperScreenLock(
    lockState: FlipperLockState,
    modifier: Modifier = Modifier,
    onChangeState: () -> Unit
) {
    val isLock = lockState is FlipperLockState.Ready && lockState.isLocked
    val lockModifier = when (lockState) {
        is FlipperLockState.Ready -> modifier
        FlipperLockState.NotInitialized,
        FlipperLockState.NotSupported -> modifier.alpha(alpha = 0.5f)
    }
    ComposableFlipperScreenOptionButton(
        modifier = lockModifier,
        iconId = if (isLock) {
            R.drawable.ic_unlock
        } else {
            R.drawable.ic_lock
        },
        titleId = if (isLock) {
            R.string.control_options_unlock
        } else {
            R.string.control_options_lock
        },
        onClick = onChangeState
    )
}

@Composable
fun ComposableFlipperScreenScreenshot(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ComposableFlipperScreenOptionButton(
        modifier = modifier,
        iconId = R.drawable.ic_camera,
        titleId = R.string.control_options_screenshot,
        onClick = onClick
    )
}

@Composable
private fun ComposableFlipperScreenOptionButton(
    @DrawableRes iconId: Int,
    @StringRes titleId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Icon(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(LocalPallet.current.flipperScreenOptionsBackground)
            .clickableRipple(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        painter = painterResource(iconId),
        contentDescription = stringResource(titleId),
        tint = LocalPallet.current.accent
    )
    Text(
        modifier = Modifier.padding(top = 8.dp),
        text = stringResource(titleId),
        color = LocalPallet.current.accent
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFlipperScreenOptionsPreview() {
    FlipperThemeInternal {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ComposableFlipperScreenScreenshot(onClick = {})
            ComposableFlipperScreenLock(
                lockState = FlipperLockState.NotInitialized,
                onChangeState = {}
            )
        }
    }
}
