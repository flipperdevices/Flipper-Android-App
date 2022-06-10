package com.flipperdevices.core.ui.ktx

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun ComposeLottiePic(
    modifier: Modifier = Modifier,
    @RawRes picResId: Int,
    @DrawableRes rollBackPicResId: Int,
    tint: Color? = null
) {
    val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(picResId))
    val composition by compositionResult
    val animateState = animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    val progress by animateState

    Box {
        if (compositionResult.isLoading) {
            if (tint == null) {
                Image(
                    modifier = modifier,
                    painter = painterResource(rollBackPicResId),
                    contentDescription = null
                )
            } else {
                Icon(
                    modifier = modifier,
                    painter = painterResource(rollBackPicResId),
                    contentDescription = null,
                    tint = tint
                )
            }
        } else {
            LottieAnimation(
                modifier = modifier,
                composition = composition,
                progress = progress
            )
        }
    }
}
