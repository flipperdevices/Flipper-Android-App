package com.flipperdevices.core.ui.ktx.image

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty

@Composable
fun ComposeLottiePic(
    @RawRes picResId: Int,
    @DrawableRes rollBackPicResId: Int,
    modifier: Modifier = Modifier,
    picModifier: Modifier = Modifier,
    tint: Color? = null
) {
    val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(picResId))
    val composition by compositionResult
    val animateState = animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    val progress by animateState

    Box(modifier = modifier) {
        if (compositionResult.isLoading) {
            if (tint == null) {
                Image(
                    modifier = picModifier,
                    painter = painterResource(rollBackPicResId),
                    contentDescription = null
                )
            } else {
                Icon(
                    modifier = picModifier,
                    painter = painterResource(rollBackPicResId),
                    contentDescription = null,
                    tint = tint
                )
            }
        } else {
            val dynamicProperties = if (tint != null) {
                rememberLottieDynamicProperties(
                    rememberLottieDynamicProperty(
                        property = LottieProperty.COLOR_FILTER,
                        value = SimpleColorFilter(tint.toArgb()), // replace to color you want
                        keyPath = arrayOf("**")
                    )
                )
            } else {
                null
            }

            LottieAnimation(
                modifier = picModifier,
                composition = composition,
                progress = { progress },
                dynamicProperties = dynamicProperties
            )
        }
    }
}
