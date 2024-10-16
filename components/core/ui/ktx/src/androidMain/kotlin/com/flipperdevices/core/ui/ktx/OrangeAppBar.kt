package com.flipperdevices.core.ui.ktx

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun OrangeAppBar(
    modifier: Modifier = Modifier,
    title: (@Composable RowScope.() -> Unit)? = null,
    startBlock: (@Composable RowScope.() -> Unit)? = null,
    endBlock: (@Composable RowScope.(Modifier) -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(42.dp)
            .background(LocalPalletV2.current.surface.navBar.body.accentBrand)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        startBlock?.invoke(this)
        title?.invoke(this)
        endBlock?.invoke(this, Modifier.padding(end = 14.dp))
    }
}

@Composable
fun OrangeAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    endBlock: (@Composable (Modifier) -> Unit)? = null
) {
    OrangeAppBar(
        modifier = modifier,
        startBlock = {
            if (onBack != null) {
                Image(
                    modifier = Modifier
                        .padding(top = 11.dp, bottom = 11.dp, start = 16.dp, end = 2.dp)
                        .size(24.dp)
                        .clickableRipple(bounded = false, onClick = onBack),
                    painter = painterResource(DesignSystem.drawable.ic_back),
                    contentDescription = null
                )
            }
        },
        title = {
            Text(
                modifier = Modifier
                    .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 11.dp)
                    .weight(1f),
                text = title,
                style = LocalTypography.current.titleB20,
                color = LocalPalletV2.current.text.label.blackOnColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        endBlock = {
            if (endBlock != null) {
                endBlock(Modifier.padding(end = 14.dp))
            }
        }
    )
}

@Composable
fun OrangeAppBarWithIcon(
    title: String,
    endIconPainter: Painter,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onEndClick: () -> Unit
) {
    OrangeAppBar(
        modifier = modifier,
        title = title,
        onBack = onBack,
        endBlock = {
            Icon(
                modifier = Modifier
                    .padding(end = 14.dp)
                    .size(24.dp)
                    .clickableRipple(onClick = onEndClick),
                painter = endIconPainter,
                contentDescription = null,
                tint = LocalPalletV2.current.icon.blackAndWhite.blackOnColor
            )
        }
    )
}

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
                    tint = LocalPalletV2.current.icon.blackAndWhite.blackOnColor
                )
            }
        )
    }
}
