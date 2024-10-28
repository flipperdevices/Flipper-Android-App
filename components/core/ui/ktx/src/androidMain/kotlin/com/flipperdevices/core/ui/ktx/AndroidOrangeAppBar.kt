package com.flipperdevices.core.ui.ktx

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2

@Composable
fun OrangeAppBar(
    @StringRes titleId: Int,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    endBlock: (@Composable (Modifier) -> Unit)? = null
) {
    OrangeAppBar(
        modifier = modifier,
        title = stringResource(titleId),
        onBack = onBack,
        endBlock = endBlock
    )
}

@Composable
fun OrangeAppBarWithIcon(
    @StringRes titleId: Int,
    @DrawableRes endIconId: Int,
    onBack: (() -> Unit)? = null,
    onEndClick: () -> Unit
) {
    OrangeAppBarWithIcon(
        title = stringResource(titleId),
        onBack = onBack,
        endIconId = endIconId,
        onEndClick = onEndClick
    )
}

@Composable
fun OrangeAppBarWithIcon(
    title: String,
    @DrawableRes endIconId: Int,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onEndClick: () -> Unit
) {
    OrangeAppBarWithIcon(
        title = title,
        endIconPainter = painterResource(endIconId),
        modifier = modifier,
        onBack = onBack,
        onEndClick = onEndClick
    )
}

@Preview
@Composable
private fun OrangeAppBarPreview() {
    FlipperThemeInternal {
        OrangeAppBar(
            title = "Screenname",
            onBack = {}
        )
    }
}

@Preview
@Composable
private fun OrangeAppBarEndBlockPreview() {
    FlipperThemeInternal {
        OrangeAppBar(
            title = "Screenname",
            onBack = {},
            endBlock = {
                Icon(
                    modifier = Modifier
                        .padding(end = 14.dp)
                        .size(24.dp),
                    painter = rememberVectorPainter(Icons.Filled.Settings),
                    contentDescription = null,
                    tint = LocalPalletV2.current.icon.blackAndWhite.default
                )
            }
        )
    }
}
