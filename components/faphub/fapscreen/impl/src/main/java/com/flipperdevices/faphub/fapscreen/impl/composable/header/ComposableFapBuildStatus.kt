package com.flipperdevices.faphub.fapscreen.impl.composable.header

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.dao.api.model.FapBuildState
import com.flipperdevices.faphub.fapscreen.impl.R

@Composable
fun ComposableFapBuildStatus(
    modifier: Modifier,
    fapBuildState: FapBuildState
) {
    when (fapBuildState) {
        FapBuildState.READY -> TODO()
        FapBuildState.BUILD_RUNNING -> TODO()
        FapBuildState.UNSUPPORTED_APP -> TODO()
        FapBuildState.FLIPPER_OUTDATED -> TODO()
        FapBuildState.UNSUPPORTED_SDK -> TODO()
    }
}

@Composable
private fun ComposableStatusCard(
    cardColor: Color,
    textColor: Color,
    @DrawableRes cardIconId: Int,
    @StringRes cardTextId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = ConstraintLayout(
    modifier = modifier
        .clip(RoundedCornerShape(8.dp))
        .background(cardColor)
        .clickableRipple(onClick = onClick)
) {
    val (icon, text, info) = createRefs()
    Icon(
        painter = painterResource(cardIconId),
        contentDescription = stringResource(cardTextId),
        tint = textColor
    )

    Text(
        text = stringResource(cardTextId),
        color = textColor,
        style = LocalTypography.current.subtitleR12,
        textAlign = TextAlign.Center
    )

    Icon(
        painter = painterResource(R.drawable.ic_info),
        contentDescription = null,

        )
}