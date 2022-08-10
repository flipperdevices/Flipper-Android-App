package com.flipperdevices.keyscreen.impl.composable.actions.common

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.flipperdevices.core.ui.ktx.ComposeLottiePic
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableActionFlipperContent(
    @DrawableRes iconId: Int,
    @StringRes textId: Int,
    @RawRes animId: Int? = null,
) {
    if (animId != null) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animId))
        val animateState by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever
        )
        LottieAnimation(
            modifier = Modifier,
            composition = composition,
            progress = { animateState }
        )
    }
    else {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = stringResource(id = textId),
            tint = LocalPallet.current.onFlipperButton
        )
    }
    Spacer(Modifier.width(6.dp))
    Text(
        text = stringResource(id = textId),
        style = LocalTypography.current.flipperAction,
        color = LocalPallet.current.onFlipperButton
    )
}
